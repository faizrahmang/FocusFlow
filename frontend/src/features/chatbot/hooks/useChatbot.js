import { useState, useEffect, useCallback, useRef } from 'react';
import api from '../../../shared/api/client';

export function useChatbot() {
  const [messages, setMessages] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [activeTaskId, setActiveTaskId] = useState(null); // null = general coaching chat
  const activeTaskIdRef = useRef(activeTaskId);
  activeTaskIdRef.current = activeTaskId;

  // Fetch history whenever the active task context changes
  useEffect(() => {
    let cancelled = false;
    setMessages([]);
    setError(null);
    api.get('/api/chatbot/history', {
      params: activeTaskId === null ? {} : { taskId: activeTaskId },
    })
      .then((res) => {
        if (!cancelled) setMessages(res.data);
      })
      .catch((err) => {
        if (!cancelled) setError(err.message);
      });
    return () => { cancelled = true; };
  }, [activeTaskId]);

  const sendMessage = useCallback(
    async (text) => {
      const trimmed = text.trim();
      if (!trimmed) return;
      const taskId = activeTaskIdRef.current;
      setError(null);

      const optimisticUser = {
        id: `local-${Date.now()}`,
        userId: null,
        taskId,
        role: 'user',
        content: trimmed,
        createdAt: new Date().toISOString(),
      };
      setMessages((prev) => [...prev, optimisticUser]);
      setLoading(true);

      try {
        const res = await api.post('/api/chatbot/message', {
          taskId,
          message: trimmed,
        });
        if (activeTaskIdRef.current === taskId) {
          setMessages((prev) => [...prev, res.data]);
        }
        return res.data;
      } catch (err) {
        if (activeTaskIdRef.current === taskId) {
          setMessages((prev) => prev.filter((m) => m.id !== optimisticUser.id));
          setError(err.message || 'The AI coach could not respond. Please try again.');
        }
        return null;
      } finally {
        setLoading(false);
      }
    },
    []
  );

  return { messages, loading, error, sendMessage, activeTaskId, setActiveTaskId };
}
