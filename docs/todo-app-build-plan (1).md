# Vibe-Coded Habit/Task/Goal App — Build Plan (React + Spring Boot)

**Goal:** Build a fully custom task/habit/goal tracker fast, by splitting work across multiple free-tier AI tools, with Claude handling architecture decisions + final integration.

---

## 1. Tech Stack

| Layer | Choice | Why |
|---|---|---|
| Frontend | React (Vite) | Lightweight, fast on low-RAM machines |
| Backend | Spring Boot 3.x (Java 17+) | REST API, mature ecosystem |
| Database | **PostgreSQL only** (relational) | Everything here (users → tasks → goals → completion logs) is inherently relational; no NoSQL needed alongside it. Free-tier hosted Postgres (Neon/Supabase/Railway) or local Docker Postgres |
| Photo storage | Cloudinary (free tier) | Photos are files, not DB rows — DB stores the URL only |
| Auth | Google OAuth2 (Spring Security) + JWT | Free, no paid service, supports multi-user out of the box |
| Chatbot model | Gemini API free tier (`gemini-flash`, via Google AI Studio) | No credit card, generous free daily quota, strong at conversational teaching/coaching — fits "help me learn a skill" better than a code-focused model |
| Charts | Recharts (MIT license, free) | Built-in animated bar/pie charts |
| Styling | Tailwind CSS | Fast theming |

**Why one DB, not "mixed":** you don't need a separate NoSQL store here — a relational DB handles structured, related data (which is exactly what tasks/goals/logs are) more simply and with fewer moving parts than running two databases. The "unstructured" part of your requirements (photos) doesn't belong in a database at all — it belongs in object storage, which we're already accounting for separately.

**Reminders — scoped for the free/simple version:** true push notifications (arriving even when the app is closed) need extra infrastructure (Web Push/VAPID keys). For the MVP, reminders work **in-app**: the frontend polls `/api/tasks/due-soon` periodically and shows a banner when you have the app open. This is free and simple; true push notifications can be a phase 2 if you want them later.

**RAM tip:** don't run Spring Boot + React dev server + a heavy AI IDE at once on a 4GB machine. Run backend → test with Postman/curl → stop it → run frontend.

---

## 2. Who Builds What (AI Tool Assignment)

| Part | Tool | Why this tool |
|---|---|---|
| **A. Backend: Entities + DB layer** | **Qwen** | Strong at structured, boilerplate-heavy entity/repository code |
| **B. Backend: Services, Controllers, Auth, Analytics, Chatbot** | **Gemini** | Solid Java/Spring support; also naturally fits building the Gemini-API-powered chatbot endpoint |
| **C. Frontend: React components + Tailwind UI (charts, forms, chatbot widget)** | **ChatGPT** | Strong at UI generation, iterating on layout/styling |
| **D. Frontend: State, auth flow, API hooks** | **Kimi** | Long context window — good for tracking many hook/state files without losing thread |
| **E. Integration, debugging, architecture review** | **Claude (me)** | Holds full context across both codebases, catches mismatches end-to-end |

Each tool works on an isolated, self-contained piece so you avoid context conflicts between tools. I do the integration pass because that needs the *whole* picture.

---

## 3. System Design (LLD) Principles

### Backend — Layered Architecture
```
Controller → Service → Repository → Database
```
- Controller: HTTP only, no business logic
- Service: business logic (recurrence calculation, goal progress %, analytics aggregation, chatbot context-building)
- Repository: Spring Data JPA, custom query methods over raw SQL where possible
- DTOs: never expose JPA entities directly — always map to `*RequestDTO`/`*ResponseDTO`

### Auth & multi-tenancy
- Every Task/Goal/CompletionLog/ChatMessage row is scoped to a `userId` — every query filters by the authenticated user, enforced at the service layer, not just the UI
- JWT issued after Google OAuth login, sent as `Authorization: Bearer <token>` on every request

### REST API Design
- Resource-based URLs (`/api/tasks`, `/api/goals`, `/api/analytics/summary`)
- Standard verbs + status codes (201 create, 204 delete, 404 not found)
- Consistent error shape: `{ "error": "TASK_NOT_FOUND", "message": "..." }`

### Frontend — Feature-based folder structure
```
src/
  features/
    tasks/       (components/, hooks/)
    goals/       (components/, hooks/)
    analytics/   (components/, hooks/)
    chatbot/     (components/, hooks/)
    auth/        (useAuth.js)
  shared/
    api/         (axios client)
    hooks/       (useDarkMode)
```
- No API calls inside components — only inside hooks
- One component = one responsibility

### Cross-cutting concerns
- CORS: allow `http://localhost:5173`
- Env config: `.env` (React) + `application.properties` (Spring Boot), API keys (Gemini, Cloudinary, DB creds) via environment variables — never hardcoded
- Validation: `@Valid` + Bean Validation annotations backend, basic form validation frontend

---

## 4. Suggested Build Order

1. **Part A** (Qwen): entities, repositories, Postgres config
2. **Part B** (Gemini): DTOs → Auth (Google OAuth2 + JWT) → Task/Goal/Analytics services+controllers → Chatbot backend (Gemini API integration + Cloudinary photo upload). Test each sub-part with Postman/curl before moving to the next.
3. **Part C** (ChatGPT): React components — task list/form, goal UI, animated bar chart + pie chart, photo-upload completion modal, chatbot panel, Google login button
4. **Part D** (Kimi): auth flow, hooks wiring every component from Part C to the real API
5. **Part E** (me): integration review — fix CORS, mismatched fields, auth edge cases, chatbot context wiring, chart data shape mismatches

---

## 5. Practical Tips for This Multi-AI Workflow

- Give each AI **one file or sub-section at a time** — Part B is large, feed it to Gemini as B1→B2→B3→B4 in the same chat so it keeps context, rather than one giant prompt
- Always paste `API_CONTRACT.md` first, into every tool, before its part
- Commit to git after each part
- Bring output back to me after each part for a quick review before moving to the next

---

## 6. Ready-to-hand-off files

| File | Give to | What it produces |
|---|---|---|
| `API_CONTRACT.md` | **Every tool** (paste first, always) | Full data model + endpoint shapes for the whole app |
| `PART_A_qwen_backend_entities.md` | Qwen | JPA entities (User, Task, Recurrence, CompletionLog, Goal, ChatMessage), repositories, Postgres config |
| `PART_B_gemini_backend_api.md` | Gemini | DTOs, Google OAuth2 + JWT auth, Task/Goal/Analytics services+controllers, Cloudinary photo upload, Gemini-API chatbot backend |
| `PART_C_chatgpt_frontend_ui.md` | ChatGPT | React components: tasks, goals, animated charts, photo-upload modal, chatbot widget, Google login button |
| `PART_D_kimi_frontend_state.md` | Kimi | Auth flow, API client, hooks (tasks/goals/analytics/chatbot), wiring into Part C's components |

**Hand-off order:** A → B → C → D → back to me for integration review.
