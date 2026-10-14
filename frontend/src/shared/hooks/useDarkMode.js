import { useState, useEffect, useCallback } from 'react';

const STORAGE_KEY = 'darkMode';

function getInitialDark() {
  if (typeof window === 'undefined') return false;

  const stored = window.localStorage.getItem(STORAGE_KEY);
  if (stored !== null) return stored === 'true';
  return window.matchMedia?.('(prefers-color-scheme: dark)').matches ?? false;
}

export function useDarkMode() {
  const [isDark, setIsDark] = useState(getInitialDark);

  useEffect(() => {
    const root = document.documentElement;
    root.classList.toggle('dark', isDark);
    root.style.colorScheme = isDark ? 'dark' : 'light';

    try {
      window.localStorage.setItem(STORAGE_KEY, String(isDark));
    } catch {
      // Storage can be unavailable in private or restricted browser contexts.
    }
  }, [isDark]);

  const toggle = useCallback(() => setIsDark((prev) => !prev), []);

  return { isDark, toggle, setIsDark };
}
