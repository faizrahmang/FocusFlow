# Part B — Backend: Services + Controllers + Auth + Analytics + Chatbot
**Tool: Gemini**
**Prerequisite: paste `API_CONTRACT.md`, then this file, then Part A's entity code (Qwen's output).**

This is the biggest part — it's split into four sub-sections below. You can hand these to Gemini as separate messages in the same chat (so it keeps context) if it's easier than one giant prompt.

---

## B1 — DTOs + Mapping
- `TaskRequestDTO`, `TaskResponseDTO`, `GoalRequestDTO`, `GoalResponseDTO` (include computed `progressPercent`), `CompletionLogDTO`, `ChatMessageDTO`, `UserDTO`
- Mapper methods/classes converting entity ↔ DTO. Never expose entities directly from controllers.
- `progressPercent` on Goal = (completed linked tasks / total linked tasks) × 100, or based on `CompletionLog` count if you prefer — pick one and document it in a code comment.

## B2 — Auth (Google OAuth2 + JWT)
- Add `spring-boot-starter-oauth2-client` and a JWT library (`jjwt` is free/open-source).
- Configure Google OAuth2 client in `application.properties` (client ID/secret come from Google Cloud Console — free to create, just needs a Google account, no billing required for OAuth consent screen + client ID).
- Flow:
  1. `GET /api/auth/google/login` → redirects to Google consent screen
  2. Google calls back → Spring Security handles the OAuth2 login → look up or create a `User` by `googleId`
  3. Issue a JWT (`sub` = userId), redirect to frontend with `?token=<jwt>` in the URL
  4. `GET /api/auth/me` reads the JWT from `Authorization: Bearer` header, returns the `User`
- Add a `JwtAuthFilter` (`OncePerRequestFilter`) that validates the JWT on every request to `/api/**` (except `/api/auth/google/**`) and sets the authenticated user in the security context so controllers can get "current user" via `@AuthenticationPrincipal` or a helper method.
- All Task/Goal/CompletionLog/ChatMessage queries must filter by the current user's ID — never return another user's data.

## B3 — Task/Goal/Analytics Service + Controller
### `TaskService`
- Standard CRUD scoped to current user
- `complete(taskId, actualDurationMinutes, note, photo)`:
  - if `photo` is present, upload it (see photo storage note below), get back a URL
  - create a `CompletionLog` row
  - if `recurrence.type != NONE`, compute the next `dueDate` (add 1 day for DAILY, 7 for WEEKLY, next matching weekday for CUSTOM_DAYS) and update the task, unless `occurrences`/`endDate` says this was the last one — then mark `archived = true` instead
- `getDueSoon()` — tasks where `reminderDateTime` is between now-1h and now

### Photo storage
- Use **Cloudinary free tier** (25 GB free storage/bandwidth, no credit card for free tier signup) — add the Cloudinary Java SDK, upload the multipart file, store the returned secure URL in `CompletionLog.photoUrl`.
- Keep this behind a small `PhotoStorageService` interface so it's easy to swap providers later without touching the rest of the code.

### `GoalService` — standard CRUD + compute `progressPercent`

### `AnalyticsService`
- `getSummary(userId, range)`: query `CompletionLog`s joined to `Task` for the given range (today / this week / this month), compute completed count, total scheduled count, completion rate, breakdown by `TaskType`, and expected-vs-actual minutes (sum `Task.expectedDurationMinutes` for completed tasks vs sum `CompletionLog.actualDurationMinutes`)
- `getTimeline(userId, range)`: group `CompletionLog`s by day (weekly range) or by day-of-month (monthly range), return counts per day for the chart

### Controllers
- `TaskController`, `GoalController`, `AnalyticsController` implementing every route in the contract

## B4 — Chatbot backend
- Add the Gemini API (Google AI Studio, free tier, `gemini-flash` model) — REST call with an API key from `https://aistudio.google.com/app/api-keys` (free, no credit card).
- `ChatbotService.sendMessage(userId, taskId, message)`:
  1. Save the user's message as a `ChatMessage`
  2. Build a system-style prompt including: the user's `personalityNotes`, and if `taskId` is present, that task's `title`, `type`, and `description` — so the bot can teach/suggest in context (e.g. "the user is learning guitar, is a beginner, prefers analogies")
  3. Call the Gemini API with this context + the message
  4. Save the assistant's reply as a `ChatMessage`, return it
- `ChatbotController` implementing `/api/chatbot/message` and `/api/chatbot/history`
- Store the Gemini API key in `application.properties` as `gemini.api.key=${GEMINI_API_KEY}` (environment variable, never hardcoded)

## Error handling (applies to everything above)
- `@RestControllerAdvice` global handler → contract's error shape
- `TaskNotFoundException`, `GoalNotFoundException`, `UnauthorizedException`, validation errors (400)

## CORS
- Allow `http://localhost:5173`, allow `Authorization` header

## Deliverable
- `dto/` package (all DTOs + mappers)
- `security/` package: OAuth2 config, `JwtAuthFilter`, JWT utility class
- `service/`: `TaskService`, `GoalService`, `AnalyticsService`, `ChatbotService`, `PhotoStorageService`
- `controller/`: `TaskController`, `GoalController`, `AnalyticsController`, `ChatbotController`, `AuthController`
- `exception/` package

## Do NOT include
- Do not redefine entities from Part A — import them
- Do not change any field/endpoint name from `API_CONTRACT.md`
