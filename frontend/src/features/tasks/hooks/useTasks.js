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
  const [completionHistory, setCompletionHistory] = useState([]);
  const [historyLoading, setHistoryLoading] = useState(true);
  const [historyError, setHistoryError] = useState(null);

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

  const refetchCompletionHistory = useCallback(async () => {
    setHistoryLoading(true);
    setHistoryError(null);
    try {
      const res = await api.get('/api/tasks/completions');
      setCompletionHistory(res.data);
      return res.data;
    } catch (err) {
      setHistoryError(err.message);
      return null;
    } finally {
      setHistoryLoading(false);
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

  useEffect(() => {
    refetchCompletionHistory();
  }, [refetchCompletionHistory]);

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
      await refetchCompletionHistory();
      return res.data; // CompletionLog
    },
    [refetch, fetchDueSoon, refetchCompletionHistory]
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
    completionHistory,
    historyLoading,
    historyError,
    refetch,
  };
}
