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
