import { useState, useMemo } from "react";
import { useAuth } from "./features/auth/useAuth";
import { useTasks } from "./features/tasks/hooks/useTasks";
import { useGoals } from "./features/goals/hooks/useGoals";
import { useAnalytics } from "./features/analytics/hooks/useAnalytics";
import { useChatbot } from "./features/chatbot/hooks/useChatbot";
import { useDarkMode } from "./shared/hooks/useDarkMode";

// Part C components
import GoogleLoginButton from "./features/auth/components/GoogleLoginButton";
import TaskList from "./features/tasks/components/TaskList";
import TaskForm from "./features/tasks/components/TaskForm";
import CompleteTaskModal from "./features/tasks/components/CompleteTaskModal";
import ProgressBarChart from "./features/analytics/components/ProgressBarChart";
import CompletionPieChart from "./features/analytics/components/CompletionPieChart";
import SummaryCards from "./features/analytics/components/SummaryCards";
import RangeToggle from "./features/analytics/components/RangeToggle";
import ChatbotPanel from "./features/chatbot/components/ChatbotPanel";
import DarkModeToggle from "./shared/components/DarkModeToggle";
import DueSoonBanner from "./shared/components/DueSoonBanner";
import GoalList from "./features/goals/components/GoalList";

export default function App() {
  const {
    user,
    isAuthenticated,
    loading: authLoading,
    error: authError,
    login,
    logout,
  } = useAuth();
  const { isDark, toggle: toggleDarkMode } = useDarkMode();

  const [typeFilter, setTypeFilter] = useState(undefined);

  const {
    tasks,
    loading: tasksLoading,
    error: tasksError,
    createTask,
    updateTask,
    deleteTask,
    reorderTasks,
    completeTask,
    dueSoonTasks,
    refetch: refetchTasks,
  } = useTasks({ type: typeFilter, archived: false });

  const { goals } = useGoals();
  const { summary, timeline, range, setRange, loading: analyticsLoading, error: analyticsError } = useAnalytics();
  const { messages, loading: chatLoading, sendMessage, activeTaskId, setActiveTaskId } = useChatbot();

  // Derive the active task title for the ChatbotPanel display
  const activeTaskTitle = useMemo(() => {
    if (!activeTaskId) return null;
    const found = tasks.find((t) => t.id === activeTaskId);
    return found ? found.title : `Task #${activeTaskId}`;
  }, [activeTaskId, tasks]);

  // Local UI state
  const [taskFormOpen, setTaskFormOpen] = useState(false);
  const [editingTask, setEditingTask] = useState(null);
  const [completingTask, setCompletingTask] = useState(null);

  const openCreateTask = () => {
    setEditingTask(null);
    setTaskFormOpen(true);
  };
  const openEditTask = (task) => {
    setEditingTask(task);
    setTaskFormOpen(true);
  };

  const handleTaskFormSubmit = async (taskRequest) => {
    if (editingTask) {
      await updateTask(editingTask.id, taskRequest);
    } else {
      await createTask(taskRequest);
    }
    setTaskFormOpen(false);
  };

  const handleCompleteSubmit = async ({ actualDurationMinutes, note, photoFile }) => {
    if (!completingTask) return;
    await completeTask(completingTask.id, { actualDurationMinutes, note, photoFile });
    setCompletingTask(null);
  };

  if (authLoading) {
    return <main className="auth-loading" role="status">Loading FocusFlow…</main>;
  }

  if (!isAuthenticated) {
    return (
      <main className="auth-shell">
        <section className="auth-story" aria-labelledby="auth-title">
          <header className="auth-story-header">
            <div className="brand-lockup">
              <span className="brand-mark" aria-hidden="true">F</span>
              <span>FocusFlow</span>
            </div>
            <DarkModeToggle isDark={isDark} onToggle={toggleDarkMode} />
          </header>

          <div className="story-copy">
            <p className="eyebrow">A calmer way to get things done</p>
            <h1 id="auth-title">Your next win starts here.</h1>
            <p>
              Turn busy days into clear, achievable progress with a todo space
              that adapts to the way you work.
            </p>
          </div>

          <div className="story-proof" aria-label="FocusFlow benefits">
            <div>
              <strong>Plan</strong>
              <span>Turn ideas into focused steps.</span>
            </div>
            <div>
              <strong>Track</strong>
              <span>See momentum build every day.</span>
            </div>
            <div>
              <strong>Reflect</strong>
              <span>Learn what helps you move forward.</span>
            </div>
          </div>
        </section>

        <section className="auth-panel" aria-labelledby="signin-title">
          <div className="auth-card">
            <div className="auth-card-heading">
              <p className="eyebrow">Welcome back</p>
              <h2 id="signin-title">Sign in to FocusFlow</h2>
              <p>Pick up where you left off and make today count.</p>
            </div>

            {authError && (
              <p className="auth-error" role="alert">
                {authError.message || String(authError)}
              </p>
            )}

            <div className="auth-alternative" aria-label="Sign in options">
              <span aria-hidden="true" />
              <p>Get started securely</p>
              <span aria-hidden="true" />
            </div>
            <GoogleLoginButton onClick={login} />
            <p className="auth-note">
              New to FocusFlow? Your account will be created when you continue
              with Google.
            </p>
          </div>
        </section>
      </main>
    );
  }

  return (
    <div className="min-h-screen bg-slate-50 text-slate-900 dark:bg-slate-950 dark:text-slate-100">
      <header className="sticky top-0 z-30 border-b border-slate-200/80 bg-white/90 backdrop-blur dark:border-slate-800 dark:bg-slate-950/90">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-3 sm:px-6">
          <div className="flex items-center gap-3">
            <div className="grid h-10 w-10 place-items-center rounded-xl bg-slate-900 font-bold text-white dark:bg-white dark:text-slate-900">F</div>
            <div>
              <h1 className="font-bold">FocusFlow</h1>
              <p className="hidden text-xs text-slate-400 sm:block">Tasks · Goals · Progress</p>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <span className="text-sm">{user?.name}</span>
            {user?.avatarUrl && <img src={user.avatarUrl} alt="" className="w-8 h-8 rounded-full" />}
            <DarkModeToggle isDark={isDark} onToggle={toggleDarkMode} />
            <button onClick={logout} className="text-sm underline">Log out</button>
          </div>
        </div>
      </header>

      <main className="mx-auto max-w-7xl px-4 py-6 sm:px-6 space-y-8">
        <DueSoonBanner tasks={dueSoonTasks} onDismiss={refetchTasks} />

        <div className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
          <div>
            <p className="text-sm font-medium text-blue-600">Good evening</p>
            <h2 className="mt-1 text-2xl font-bold tracking-tight sm:text-3xl">Stay consistent. Make progress.</h2>
          </div>
          <button onClick={openCreateTask} className="self-start px-3 py-1 bg-blue-600 text-white rounded-xl text-sm font-semibold">
            + New task
          </button>
        </div>

        <select
          value={typeFilter ?? ""}
          onChange={(e) => setTypeFilter(e.target.value || undefined)}
          className="border rounded p-1 dark:bg-slate-800 dark:border-slate-700"
        >
          <option value="">All types</option>
          <option value="HABIT">Habits</option>
          <option value="TASK">Tasks</option>
          <option value="SKILL">Skills</option>
          <option value="HEALTH">Health</option>
        </select>

        {tasksError && <p className="text-red-500 text-sm">{tasksError}</p>}

        {tasksLoading ? (
          <p className="text-sm text-slate-500">Loading tasks…</p>
        ) : (
          <TaskList
            tasks={tasks}
            onComplete={setCompletingTask}
            onArchive={(id) => console.log("archive task", id)}
            onDelete={deleteTask}
            onReorder={reorderTasks}
            onSelect={openEditTask}
          />
        )}

        <section className="space-y-4">
          <h3 className="text-lg font-semibold">My Goals</h3>
          <GoalList goals={goals} />
        </section>

        <section className="space-y-5">
          <h3 className="text-lg font-semibold">Progress &amp; Analytics</h3>
          <RangeToggle selected={range} onSelect={setRange} />
          {analyticsError && <p className="text-red-500 text-sm">{analyticsError}</p>}
          {analyticsLoading ? (
            <p className="text-sm text-slate-500">Loading analytics…</p>
          ) : (
            <>
              {summary && <SummaryCards summary={summary} />}
              <div className="grid gap-5 lg:grid-cols-[minmax(0,1.5fr)_minmax(280px,1fr)]">
                <div className="rounded-2xl border border-slate-200 bg-white p-5 dark:border-slate-800 dark:bg-slate-900">
                  <h4 className="font-semibold">Progress over time</h4>
                  <ProgressBarChart data={timeline} />
                </div>
                <div className="rounded-2xl border border-slate-200 bg-white p-5 dark:border-slate-800 dark:bg-slate-900">
                  <h4 className="font-semibold">Completion by type</h4>
                  {summary && <CompletionPieChart data={summary.byType || {}} />}
                </div>
              </div>
            </>
          )}
        </section>

        <ChatbotPanel
          messages={messages}
          loading={chatLoading}
          onSend={sendMessage}
          activeTaskTitle={activeTaskTitle}
        />
      </main>

      {/* Task form overlay */}
      {taskFormOpen && (
        <div className="fixed inset-0 z-50 grid place-items-center bg-slate-950/60 p-4">
          <div className="w-full max-w-2xl max-h-[90vh] overflow-y-auto">
            <TaskForm
              initialValues={editingTask}
              goals={goals}
              onSubmit={handleTaskFormSubmit}
              onCancel={() => setTaskFormOpen(false)}
            />
          </div>
        </div>
      )}

      {/* Complete task modal */}
      {completingTask && (
        <CompleteTaskModal
          task={completingTask}
          onSubmit={handleCompleteSubmit}
          onCancel={() => setCompletingTask(null)}
        />
      )}
    </div>
  );
}
