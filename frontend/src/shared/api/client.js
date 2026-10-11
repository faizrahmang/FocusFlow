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
