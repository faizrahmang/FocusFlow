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
    window.location.href = `${apiUrl}/oauth2/authorization/google`;
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
