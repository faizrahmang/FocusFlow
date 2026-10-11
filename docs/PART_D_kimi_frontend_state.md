# Part D — Frontend: State Management + API Integration
**Tool: Kimi**
**Prerequisite: paste `API_CONTRACT.md`, then this file, then Part C's component code (ChatGPT's output).**

## Scope
API client, auth flow, hooks, local UI state. Wire into Part C's components using their exact prop names.

## What to generate

### 1. API client — `src/shared/api/client.js`
- Axios instance, base URL from `VITE_API_URL`
- Request interceptor: attach `Authorization: Bearer <token>` from stored JWT (see auth below) to every request
- Response interceptor: on 401, clear stored token and redirect to login
- Normalize errors to the contract's `{ error, message }` shape

### 2. Auth — `src/features/auth/useAuth.js`
- On app load, check `localStorage.jwt`. If a `?token=` query param is present (after Google redirect), save it to `localStorage`, strip it from the URL, and fetch `/api/auth/me`
- Exposes `{ user, isAuthenticated, login, logout }` — `login()` just redirects to `${VITE_API_URL}/api/auth/google/login`
- `logout()` clears `localStorage.jwt` and resets state

### 3. `useTasks` hook
Returns:
```js
{
  tasks, loading, error,
  createTask, updateTask, deleteTask,
  reorderTasks,               // (orderedIds) => Promise
  completeTask,                // (id, { actualDurationMinutes, note, photoFile }) => Promise — builds FormData for multipart upload
  dueSoonTasks,                 // from GET /api/tasks/due-soon, polled every ~60s
  refetch,
}
```
- Support the `type`/`archived`/`goalId` filters as hook params, passed as query string

### 4. `useGoals` hook — CRUD wrapper around `/api/goals`

### 5. `useAnalytics` hook
```js
{
  summary,      // GET /api/analytics/summary?range=...
  timeline,     // GET /api/analytics/timeline?range=...
  range, setRange,  // "daily" | "weekly" | "monthly"
  loading,
}
```

### 6. `useChatbot` hook
```js
{
  messages, loading,
  sendMessage,   // (text) => Promise — posts to /api/chatbot/message with current activeTaskId
  activeTaskId, setActiveTaskId,  // null = general coaching chat
}
```
- Fetch history via `/api/chatbot/history?taskId=` whenever `activeTaskId` changes

### 7. Dark mode — `src/shared/hooks/useDarkMode.js` (same as before: localStorage + `dark` class toggle)

### 8. Wire into `App.jsx`
- If not authenticated → show `<GoogleLoginButton onClick={login} />`
- If authenticated → render the full app, passing hook data into Part C's components using their exact prop names
- Example:
```jsx
const { tasks, completeTask, dueSoonTasks } = useTasks();
const { summary, timeline, range, setRange } = useAnalytics();
<TaskList tasks={tasks} onComplete={openCompleteModal} ... />
<DueSoonBanner tasks={dueSoonTasks} onDismiss={dismiss} />
<ProgressBarChart data={timeline} />
<CompletionPieChart data={summary.byType} />
```

## Deliverable
- `src/shared/api/client.js`
- `.env` (`VITE_API_URL=http://localhost:8080`)
- `src/features/auth/useAuth.js`
- `src/features/tasks/hooks/useTasks.js`
- `src/features/goals/hooks/useGoals.js`
- `src/features/analytics/hooks/useAnalytics.js`
- `src/features/chatbot/hooks/useChatbot.js`
- `src/shared/hooks/useDarkMode.js`
- Updated `App.jsx`

## Do NOT include
- Do not redefine or restyle Part C's components — only wire data into them
- Do not change any field name from `API_CONTRACT.md`
