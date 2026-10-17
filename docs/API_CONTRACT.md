# API Contract — Todo/Habit/Goal App (SOURCE OF TRUTH)

Paste this into every AI tool before giving them their part. This supersedes the earlier simple-todo contract.

---

## Data Model

### User
| Field | Type | Notes |
|---|---|---|
| `id` | number | |
| `googleId` | string | from Google OAuth |
| `email` | string | |
| `name` | string | |
| `avatarUrl` | string | from Google profile |
| `personalityNotes` | string | free text the user writes about themselves — fed to the chatbot for personalization (e.g. "I learn best with analogies, I'm a beginner in guitar") |

### Task
| Field | Type | Notes |
|---|---|---|
| `id` | number | |
| `userId` | number | owner |
| `title` | string | required |
| `description` | string | optional |
| `type` | `"HABIT" \| "TASK" \| "SKILL" \| "HEALTH"` | classification |
| `priority` | number (1–5) | 1 = lowest, 5 = highest |
| `goalId` | number \| null | optional link to a Goal |
| `dueDate` | string (ISO date) | nullable |
| `dueTime` | string (`HH:mm`) | nullable — time-of-day deadline |
| `expectedDurationMinutes` | number | how long the user expects it to take |
| `reminderDateTime` | string (ISO datetime) | nullable |
| `recurrence` | object | see Recurrence below |
| `archived` | boolean | |
| `position` | number | for ordering |
| `createdAt` / `updatedAt` | string (ISO datetime) | |

### Recurrence (embedded object on Task)
```json
{
  "type": "NONE",           // NONE | DAILY | WEEKLY | CUSTOM_DAYS | FOR_N_DAYS
  "daysOfWeek": ["MON","WED","FRI"],  // used when type = CUSTOM_DAYS
  "occurrences": 30,        // used when type = FOR_N_DAYS (repeat for N days total)
  "endDate": "2026-12-31"   // optional cap for DAILY/WEEKLY/CUSTOM_DAYS
}
```

### CompletionLog
One row per completed occurrence (this is how daily/weekly/monthly analytics and habit streaks are computed).
| Field | Type | Notes |
|---|---|---|
| `id` | number | |
| `taskId` | number | |
| `completedAt` | string (ISO datetime) | when it was actually marked done |
| `actualDurationMinutes` | number | nullable, user-entered |
| `photoUrl` | string | nullable, proof-of-completion photo |
| `note` | string | nullable |

### Goal
| Field | Type | Notes |
|---|---|---|
| `id` | number | |
| `userId` | number | |
| `title` | string | e.g. "Learn guitar basics" |
| `type` | `"HABIT" \| "TASK" \| "SKILL" \| "HEALTH"` | |
| `targetDate` | string (ISO date) | nullable |
| `linkedTaskIds` | number[] | tasks contributing to this goal |

### ChatMessage (chatbot)
| Field | Type | Notes |
|---|---|---|
| `id` | number | |
| `userId` | number | |
| `taskId` | number \| null | optional context (which task/skill/habit this relates to) |
| `role` | `"user" \| "assistant"` | |
| `content` | string | |
| `createdAt` | string (ISO datetime) | |

---

## Endpoints

### Auth
| Method | Path | Notes |
|---|---|---|
| GET | `/api/auth/google/login` | redirects to Google OAuth consent screen |
| GET | `/api/auth/google/callback` | Google redirects here; server issues a JWT, redirects to frontend with token |
| GET | `/api/auth/me` | returns current `User` (requires `Authorization: Bearer <jwt>`) |

**All endpoints below require `Authorization: Bearer <jwt>` and are scoped to the logged-in user.**

### Tasks
| Method | Path | Body | Response |
|---|---|---|---|
| GET | `/api/tasks` | — | `Task[]`, query params: `?type=HABIT&archived=false&goalId=3` |
| GET | `/api/tasks/{id}` | — | `Task` |
| POST | `/api/tasks` | `TaskRequest` | `Task` (201) |
| PUT | `/api/tasks/{id}` | `TaskRequest` | `Task` |
| DELETE | `/api/tasks/{id}` | — | 204 |
| PATCH | `/api/tasks/reorder` | `[{id, position}]` | `Task[]` |
| POST | `/api/tasks/{id}/complete` | multipart: `actualDurationMinutes`, `note`, `photo` (file, optional) | `CompletionLog` (201) — creates a completion log entry; recurring tasks compute the next due date, and non-recurring or finished recurring tasks are archived |
| GET | `/api/tasks/{id}/logs` | — | `CompletionLog[]` — history for one task |
| GET | `/api/tasks/due-soon` | — | `Task[]` — tasks with `reminderDateTime` in the past/next hour, for in-app reminders |

### Goals
| Method | Path | Body | Response |
|---|---|---|---|
| GET | `/api/goals` | — | `Goal[]` (each includes computed `progressPercent`) |
| POST | `/api/goals` | `GoalRequest` | `Goal` (201) |
| PUT | `/api/goals/{id}` | `GoalRequest` | `Goal` |
| DELETE | `/api/goals/{id}` | — | 204 |

### Analytics
| Method | Path | Response |
|---|---|---|
| GET | `/api/analytics/summary?range=daily\|weekly\|monthly` | `{ completed: number, total: number, completionRate: number, byType: {HABIT: n, TASK: n, SKILL: n, HEALTH: n}, expectedVsActualMinutes: {expected: n, actual: n} }` |
| GET | `/api/analytics/timeline?range=weekly\|monthly` | `[{ date: "2026-09-20", completed: n, total: n }]` — for the bar/line chart over time |

### Chatbot
| Method | Path | Body | Response |
|---|---|---|---|
| POST | `/api/chatbot/message` | `{ "taskId": number\|null, "message": string }` | `ChatMessage` (assistant's reply) — server includes task context + `personalityNotes` when calling Gemini |
| GET | `/api/chatbot/history?taskId={id}` | — | `ChatMessage[]` |

### TaskRequest (POST/PUT body)
```json
{
  "title": "Practice guitar",
  "description": "Scales + one song",
  "type": "SKILL",
  "priority": 4,
  "goalId": 2,
  "dueDate": "2026-10-01",
  "dueTime": "18:00",
  "expectedDurationMinutes": 30,
  "reminderDateTime": "2026-10-01T17:45:00Z",
  "recurrence": { "type": "DAILY", "occurrences": 30 }
}
```

### Error shape (all 4xx/5xx)
```json
{ "error": "TASK_NOT_FOUND", "message": "Task with id 5 not found" }
```

---

## Global rules every part must follow
- Base URL: `http://localhost:8080`, frontend `VITE_API_URL`
- CORS: allow `http://localhost:5173`
- All dates ISO 8601, times as `HH:mm` 24-hour
- JWT sent as `Authorization: Bearer <token>` on every request except `/api/auth/google/*`
- No field renaming without updating this file first and re-sharing with all tools
