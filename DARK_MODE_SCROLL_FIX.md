# Dark mode and scrolling fix

## What changed

- `src/shared/hooks/useDarkMode.js` — the hook now applies the `dark` class directly to
  `<html>`, updates the browser `color-scheme`, persists the choice to `localStorage`
  (wrapped in try/catch for private/restricted browsing contexts), and exposes `setIsDark`
  alongside `isDark` and `toggle`.
- `src/index.css` — added the Tailwind v4 custom variant `@custom-variant dark (&:where(.dark, .dark *));`
  so `dark:` utilities respond to the `dark` class instead of only the OS preference, plus
  `overflow-x: hidden` and a light/dark `color` + background transition on `body`.

## Why

- Tailwind v4 uses the system preference for `dark:` by default. This app owns the theme with
  the `dark` class on `<html>`, so the custom variant makes that contract explicit.
- `overflow-x: hidden` prevents horizontal overflow while leaving normal vertical scrolling
  enabled. `min-height: 100vh` ensures the page fills the viewport without forcing a fixed height.

## Verify locally

```bash
npm install
npm run dev
```

Open the local URL shown by Vite, usually `http://localhost:5173`.

1. Clicking the theme toggle changes the page colors immediately.
2. Refreshing the page preserves the selected theme.
3. The page scrolls vertically when content is taller than the viewport.
4. There is no unwanted horizontal scrollbar.
