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
