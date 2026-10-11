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
