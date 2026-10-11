# Part D — State Management + API Integration (single file)

// =============================================================================
// FILE: .env
// =============================================================================

```env
VITE_API_URL=http://localhost:8080
```

// =============================================================================
// FILE: src/shared/api/client.js
// =============================================================================

```js
import axios from 'axios';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

export const TOKEN_KEY = 'jwt';

export const getToken = () => localStorage.getItem(TOKEN_KEY);
export const setToken = (token) => localStorage.setItem(TOKEN_KEY, token);
export const clearToken = () => localStorage.removeItem(TOKEN_KEY);

const api = axios.create({
  baseURL: API_URL,
  headers: { 'Content-Type': 'application/json' },
});

// Attach JWT to every request
api.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// On 401, clear token and bounce to login
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      clearToken();
      // Strip any stale route and force the unauthenticated view
      window.location.href = '/';
    }
    // Normalize to the contract's { error, message } shape
    const normalized = {
      error: error.response?.data?.error || 'REQUEST_FAILED',
      message:
        error.response?.data?.message ||
        error.message ||
        'Something went wrong. Please try again.',
    };
    return Promise.reject(normalized);
  }
);

export default api;
```

// =============================================================================
// FILE: src/features/auth/useAuth.js
// =============================================================================

```js
import { useState, useEffect, useCallback } from 'react';
import api, { getToken, setToken, clearToken } from '../../shared/api/client';

export function useAuth() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    // 1) Google OAuth redirect lands here with ?token=<jwt>
    const params = new URLSearchParams(window.location.search);
    const tokenFromUrl = params.get('token');
    if (tokenFromUrl) {
      setToken(tokenFromUrl);
      params.delete('token');
      const query = params.toString();
      window.history.replaceState(
        {},
        '',
        window.location.pathname + (query ? `?${query}` : '')
      );
    }

    // 2) If we have a token, hydrate the current user
    const token = getToken();
    if (!token) {
      setLoading(false);
      return;
    }

    let cancelled = false;
    api
      .get('/api/auth/me')
      .then((res) => {
        if (!cancelled) setUser(res.data);
      })
      .catch((err) => {
        if (!cancelled) {
          setError(err.message);
          setUser(null);
        }
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });

    return () => { cancelled = true; };
  }, []);

  // Redirect browser to the Google OAuth consent screen
  const login = useCallback(() => {
    const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';
    window.location.href = `${apiUrl}/api/auth/google/login`;
  }, []);

  const logout = useCallback(() => {
    clearToken();
    setUser(null);
    setError(null);
  }, []);

  return {
    user,
    loading,
    error,
    isAuthenticated: !!user,
    login,
    logout,
  };
}
```

// =============================================================================
// FILE: src/features/tasks/hooks/useTasks.js
// =============================================================================

```js
import { useState, useEffect, useCallback, useRef } from 'react';
import api from '../../../shared/api/client';

const DUE_SOON_POLL_MS = 60_000;

/**
 * @param {{ type?: string, archived?: boolean, goalId?: number }} filters
 */
export function useTasks(filters = {}) {
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [dueSoonTasks, setDueSoonTasks] = useState([]);

  // Keep latest filters in a ref so stable callbacks always use current values
  const filtersRef = useRef(filters);
  filtersRef.current = filters;

  const buildParams = useCallback(() => {
    const params = {};
    const f = filtersRef.current;
    if (f.type !== undefined) params.type = f.type;
    if (f.archived !== undefined) params.archived = f.archived;
    if (f.goalId !== undefined) params.goalId = f.goalId;
    return params;
  }, []);

  const refetch = useCallback(async () => {
    try {
      setError(null);
      const res = await api.get('/api/tasks', { params: buildParams() });
      setTasks(res.data);
      return res.data;
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, [buildParams]);

  const fetchDueSoon = useCallback(async () => {
    try {
      const res = await api.get('/api/tasks/due-soon');
      setDueSoonTasks(res.data);
    } catch (err) {
      // due-soon is a nice-to-have; don't surface as a list error
      console.warn('due-soon fetch failed:', err.message);
    }
  }, []);

  // Initial load + refetch whenever filters change
  useEffect(() => {
    setLoading(true);
    refetch().catch(() => {});
  }, [filters.type, filters.archived, filters.goalId, refetch]);

  // Poll due-soon every ~60s
  useEffect(() => {
    fetchDueSoon();
    const id = setInterval(fetchDueSoon, DUE_SOON_POLL_MS);
    return () => clearInterval(id);
  }, [fetchDueSoon]);

  const createTask = useCallback(
    async (taskRequest) => {
      const res = await api.post('/api/tasks', taskRequest);
      await refetch();
      return res.data;
    },
    [refetch]
  );

  const updateTask = useCallback(
    async (id, taskRequest) => {
      const res = await api.put(`/api/tasks/${id}`, taskRequest);
      setTasks((prev) => prev.map((t) => (t.id === id ? res.data : t)));
      return res.data;
    },
    []
  );

  const deleteTask = useCallback(
    async (id) => {
      await api.delete(`/api/tasks/${id}`);
      setTasks((prev) => prev.filter((t) => t.id !== id));
    },
    []
  );

  const reorderTasks = useCallback(
    async (orderedIds) => {
      // Optimistic update
      setTasks((prev) => {
        const byId = new Map(prev.map((t) => [t.id, t]));
        return orderedIds.map((id, index) => {
          const task = byId.get(id);
          return task ? { ...task, position: index } : task;
        }).filter(Boolean);
      });
      const body = orderedIds.map((id, index) => ({ id, position: index }));
      const res = await api.patch('/api/tasks/reorder', body);
      await refetch();
      return res.data;
    },
    [refetch]
  );

  /**
   * Mark a task complete. Builds multipart FormData for photo upload.
   * @param {number} id
   * @param {{ actualDurationMinutes?: number, note?: string, photoFile?: File }} payload
   */
  const completeTask = useCallback(
    async (id, { actualDurationMinutes, note, photoFile } = {}) => {
      const formData = new FormData();
      if (actualDurationMinutes !== undefined && actualDurationMinutes !== null) {
        formData.append('actualDurationMinutes', String(actualDurationMinutes));
      }
      if (note) formData.append('note', note);
      if (photoFile) formData.append('photo', photoFile);

      const res = await api.post(`/api/tasks/${id}/complete`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      await refetch();
      await fetchDueSoon();
      return res.data; // CompletionLog
    },
    [refetch, fetchDueSoon]
  );

  return {
    tasks,
    loading,
    error,
    createTask,
    updateTask,
    deleteTask,
    reorderTasks,
    completeTask,
    dueSoonTasks,
    refetch,
  };
}
```

// =============================================================================
// FILE: src/features/goals/hooks/useGoals.js
// =============================================================================

```js
import { useState, useEffect, useCallback } from 'react';
import api from '../../../shared/api/client';

export function useGoals() {
  const [goals, setGoals] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const refetch = useCallback(async () => {
    try {
      setError(null);
      const res = await api.get('/api/goals');
      setGoals(res.data); // each Goal includes computed progressPercent
      return res.data;
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    refetch().catch(() => {});
  }, [refetch]);

  const createGoal = useCallback(
    async (goalRequest) => {
      const res = await api.post('/api/goals', goalRequest);
      await refetch();
      return res.data;
    },
    [refetch]
  );

  const updateGoal = useCallback(
    async (id, goalRequest) => {
      const res = await api.put(`/api/goals/${id}`, goalRequest);
      await refetch();
      return res.data;
    },
    [refetch]
  );

  const deleteGoal = useCallback(
    async (id) => {
      await api.delete(`/api/goals/${id}`);
      await refetch();
    },
    [refetch]
  );

  return { goals, loading, error, createGoal, updateGoal, deleteGoal, refetch };
}
```

// =============================================================================
// FILE: src/features/analytics/hooks/useAnalytics.js
// =============================================================================

```js
import { useState, useEffect, useCallback } from 'react';
import api from '../../../shared/api/client';

export function useAnalytics() {
  const [range, setRange] = useState('daily'); // "daily" | "weekly" | "monthly"
  const [summary, setSummary] = useState(null);
  const [timeline, setTimeline] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchAnalytics = useCallback(async (currentRange) => {
    try {
      setError(null);
      setLoading(true);
      const [summaryRes, timelineRes] = await Promise.all([
        api.get('/api/analytics/summary', { params: { range: currentRange } }),
        // timeline only supports weekly|monthly per contract
        ['weekly', 'monthly'].includes(currentRange)
          ? api.get('/api/analytics/timeline', { params: { range: currentRange } })
          : Promise.resolve({ data: [] }),
      ]);
      setSummary(summaryRes.data);
      setTimeline(timelineRes.data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchAnalytics(range);
  }, [range, fetchAnalytics]);

  return { summary, timeline, range, setRange, loading, error, refetch: () => fetchAnalytics(range) };
}
```

// =============================================================================
// FILE: src/features/chatbot/hooks/useChatbot.js
// =============================================================================

```js
import { useState, useEffect, useCallback, useRef } from 'react';
import api from '../../../shared/api/client';

export function useChatbot() {
  const [messages, setMessages] = useState([]);
  const [loading, setLoading] = useState(false);
  const [activeTaskId, setActiveTaskId] = useState(null); // null = general coaching chat
  const activeTaskIdRef = useRef(activeTaskId);
  activeTaskIdRef.current = activeTaskId;

  // Fetch history whenever the active task context changes
  useEffect(() => {
    let cancelled = false;
    setMessages([]);
    if (activeTaskId === null) {
      return; // general chat: no history to load (or load all history if backend supports it)
    }
    api
      .get('/api/chatbot/history', { params: { taskId: activeTaskId } })
      .then((res) => {
        if (!cancelled) setMessages(res.data);
      })
      .catch((err) => console.warn('chat history fetch failed:', err.message));
    return () => { cancelled = true; };
  }, [activeTaskId]);

  const sendMessage = useCallback(
    async (text) => {
      const trimmed = text.trim();
      if (!trimmed) return;

      const optimisticUser = {
        id: `local-${Date.now()}`,
        userId: null,
        taskId: activeTaskIdRef.current,
        role: 'user',
        content: trimmed,
        createdAt: new Date().toISOString(),
      };
      setMessages((prev) => [...prev, optimisticUser]);
      setLoading(true);

      try {
        const res = await api.post('/api/chatbot/message', {
          taskId: activeTaskIdRef.current,
          message: trimmed,
        });
        setMessages((prev) => [...prev, res.data]);
        return res.data;
      } catch (err) {
        // Roll back the optimistic message on failure
        setMessages((prev) => prev.filter((m) => m.id !== optimisticUser.id));
        throw err;
      } finally {
        setLoading(false);
      }
    },
    []
  );

  return { messages, loading, sendMessage, activeTaskId, setActiveTaskId };
}
```

// =============================================================================
// FILE: src/shared/hooks/useDarkMode.js
// =============================================================================

```js
import { useState, useEffect, useCallback } from 'react';

const STORAGE_KEY = 'darkMode';

function getInitialDark() {
  const stored = localStorage.getItem(STORAGE_KEY);
  if (stored !== null) return stored === 'true';
  return window.matchMedia?.('(prefers-color-scheme: dark)').matches ?? false;
}

export function useDarkMode() {
  const [isDark, setIsDark] = useState(getInitialDark);

  useEffect(() => {
    localStorage.setItem(STORAGE_KEY, String(isDark));
    document.documentElement.classList.toggle('dark', isDark);
  }, [isDark]);

  const toggle = useCallback(() => setIsDark((prev) => !prev), []);

  return { isDark, toggle, setIsDark };
}
```

// =============================================================================
// FILE: src/App.jsx
// =============================================================================

```jsx
import { useState } from 'react';
import { useAuth } from './features/auth/useAuth';
import { useTasks } from './features/tasks/hooks/useTasks';
import { useGoals } from './features/goals/hooks/useGoals';
import { useAnalytics } from './features/analytics/hooks/useAnalytics';
import { useChatbot } from './features/chatbot/hooks/useChatbot';
import { useDarkMode } from './shared/hooks/useDarkMode';

// ---------------------------------------------------------------------------
// Part C components — imports must match Part C's actual file layout.
// Prop names below follow the contract / Part D spec exactly.
// ---------------------------------------------------------------------------
import GoogleLoginButton from './features/auth/GoogleLoginButton'; // { onClick }
import TaskList from './features/tasks/TaskList'; // { tasks, loading, onComplete, onEdit, onDelete, onReorder }
import TaskFormModal from './features/tasks/TaskFormModal'; // { open, initialTask, onSubmit, onClose }
import CompleteTaskModal from './features/tasks/CompleteTaskModal'; // { task, onSubmit, onClose }
import DueSoonBanner from './features/tasks/DueSoonBanner'; // { tasks, onDismiss }
import GoalList from './features/goals/GoalList'; // { goals, onCreate, onUpdate, onDelete }
import ProgressBarChart from './features/analytics/ProgressBarChart'; // { data: timeline }
import CompletionPieChart from './features/analytics/CompletionPieChart'; // { data: summary.byType }
import RangeSelector from './features/analytics/RangeSelector'; // { range, onChange }
import ChatPanel from './features/chatbot/ChatPanel'; // { messages, loading, onSend, activeTaskId, onSetActiveTaskId }
import DarkModeToggle from './shared/components/DarkModeToggle'; // { isDark, onToggle }

export default function App() {
  const { user, isAuthenticated, loading: authLoading, login, logout } = useAuth();
  const { isDark, toggle: toggleDarkMode } = useDarkMode();

  const [typeFilter, setTypeFilter] = useState(undefined);
  const {
    tasks, loading: tasksLoading, error: tasksError,
    createTask, updateTask, deleteTask, reorderTasks,
    completeTask, dueSoonTasks, refetch: refetchTasks,
  } = useTasks({ type: typeFilter, archived: false });

  const { goals, createGoal, updateGoal, deleteGoal } = useGoals();
  const { summary, timeline, range, setRange, loading: analyticsLoading } = useAnalytics();
  const {
    messages, loading: chatLoading,
    sendMessage, activeTaskId, setActiveTaskId,
  } = useChatbot();

  // Local UI state
  const [taskFormOpen, setTaskFormOpen] = useState(false);
  const [editingTask, setEditingTask] = useState(null);
  const [completingTask, setCompletingTask] = useState(null);

  const openCreateModal = () => { setEditingTask(null); setTaskFormOpen(true); };
  const openEditModal = (task) => { setEditingTask(task); setTaskFormOpen(true); };

  const handleTaskFormSubmit = async (taskRequest) => {
    if (editingTask) {
      await updateTask(editingTask.id, taskRequest);
    } else {
      await createTask(taskRequest);
    }
    setTaskFormOpen(false);
  };

  const handleCompleteSubmit = async ({ actualDurationMinutes, note, photoFile }) => {
    await completeTask(completingTask.id, { actualDurationMinutes, note, photoFile });
    setCompletingTask(null);
  };

  if (authLoading) {
    return <div className="min-h-screen flex items-center justify-center">Loading…</div>;
  }

  if (!isAuthenticated) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-gray-900">
        <GoogleLoginButton onClick={login} />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900 text-gray-900 dark:text-gray-100">
      <header className="flex items-center justify-between p-4 border-b dark:border-gray-700">
        <h1 className="text-xl font-bold">Todo / Habit / Goal Tracker</h1>
        <div className="flex items-center gap-3">
          <span className="text-sm">{user?.name}</span>
          {user?.avatarUrl && <img src={user.avatarUrl} alt="" className="w-8 h-8 rounded-full" />}
          <DarkModeToggle isDark={isDark} onToggle={toggleDarkMode} />
          <button onClick={logout} className="text-sm underline">Log out</button>
        </div>
      </header>

      <main className="p-4 grid gap-6 lg:grid-cols-3">
        <section className="lg:col-span-2 space-y-4">
          <DueSoonBanner tasks={dueSoonTasks} onDismiss={refetchTasks} />

          <div className="flex items-center justify-between">
            <select
              value={typeFilter ?? ''}
              onChange={(e) => setTypeFilter(e.target.value || undefined)}
              className="border rounded p-1 dark:bg-gray-800"
            >
              <option value="">All types</option>
              <option value="HABIT">Habits</option>
              <option value="TASK">Tasks</option>
              <option value="SKILL">Skills</option>
              <option value="HEALTH">Health</option>
            </select>
            <button onClick={openCreateModal} className="px-3 py-1 bg-blue-600 text-white rounded">
              + New
            </button>
          </div>

          {tasksError && <p className="text-red-500 text-sm">{tasksError}</p>}

          <TaskList
            tasks={tasks}
            loading={tasksLoading}
            onComplete={setCompletingTask}
            onEdit={openEditModal}
            onDelete={deleteTask}
            onReorder={reorderTasks}
          />

          <GoalList goals={goals} onCreate={createGoal} onUpdate={updateGoal} onDelete={deleteGoal} />
        </section>

        <aside className="space-y-4">
          <RangeSelector range={range} onChange={setRange} />
          {analyticsLoading ? (
            <p className="text-sm text-gray-500">Loading analytics…</p>
          ) : (
            <>
              <ProgressBarChart data={timeline} />
              {summary && <CompletionPieChart data={summary.byType} />}
            </>
          )}

          <ChatPanel
            messages={messages}
            loading={chatLoading}
            onSend={sendMessage}
            activeTaskId={activeTaskId}
            onSetActiveTaskId={setActiveTaskId}
          />
        </aside>
      </main>

      <TaskFormModal
        open={taskFormOpen}
        initialTask={editingTask}
        goals={goals}
        onSubmit={handleTaskFormSubmit}
        onClose={() => setTaskFormOpen(false)}
      />

      <CompleteTaskModal
        task={completingTask}
        onSubmit={handleCompleteSubmit}
        onClose={() => setCompletingTask(null)}
      />
    </div>
  );
}
```
