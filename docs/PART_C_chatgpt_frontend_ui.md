# Part C — Frontend: React Components + Tailwind UI
**Tool: ChatGPT**
**Prerequisite: paste `API_CONTRACT.md`, then this file.**

## Scope
Presentational components only — no API calls, no fetch/axios. Everything comes in via props, actions go out via callback props. Part D wires real data in. **Don't rename the props below.**

## Tech
- React + Vite, Tailwind CSS
- Charts: use `recharts` (free, MIT-licensed) for the bar chart and pie chart — it has smooth built-in animations (bars animate in left-to-right by default with `<Bar>` + `isAnimationActive`)
- Light/dark mode via Tailwind's `dark:` variant

## Components to build

### Task list & items
- `<TaskList tasks={Task[]} onComplete={(id) => {}} onArchive={(id) => {}} onDelete={(id) => {}} onReorder={(ids) => {}} onSelect={(id) => {}} />`
  - Group or filter visually by `type` (tabs or colored section headers: Habit / Task / Skill / Health)
  - Show priority as 1–5 filled dots or a colored bar (5 = red/urgent, 1 = gray)
- `<TaskItem task={Task} onComplete onArchive onDelete onSelect />`
  - Shows due date + due time, expected duration, recurrence badge (e.g. "Daily", "3x/week")
- `<TaskForm initialValues={Task|null} goals={Goal[]} onSubmit={(data: TaskRequest) => {}} onCancel={() => {}} />`
  - Fields: title, description, type (4-way toggle: Habit/Task/Skill/Health), priority (1–5 slider or star picker), goal (dropdown from `goals` prop, optional), due date, due time, expected duration (minutes), reminder date/time, recurrence (select NONE/DAILY/WEEKLY/CUSTOM_DAYS/FOR_N_DAYS, with conditional sub-fields: day-of-week checkboxes for CUSTOM_DAYS, a number input for FOR_N_DAYS, optional end date)

### Completion flow
- `<CompleteTaskModal task={Task} onSubmit={({ actualDurationMinutes, note, photoFile }) => {}} onCancel={() => {}} />`
  - Shows expected duration for comparison, input for actual duration, optional note, optional photo upload (`<input type="file" accept="image/*" capture="environment">` so it can use the camera on mobile)

### Goals
- `<GoalList goals={Goal[]} onSelect={(id) => {}} onDelete={(id) => {}} />`
  - Each goal shows a progress bar using `goal.progressPercent`
- `<GoalForm initialValues={Goal|null} tasks={Task[]} onSubmit={(data: GoalRequest) => {}} onCancel={() => {}} />`

### Analytics
- `<ProgressBarChart data={{date: string, completed: number, total: number}[]} />`
  - Animated bar chart (recharts `<BarChart>`), bars grow left-to-right on mount
- `<CompletionPieChart data={{HABIT: number, TASK: number, SKILL: number, HEALTH: number}} />`
  - Pie/donut chart showing completion breakdown by type
- `<SummaryCards summary={{completed, total, completionRate, expectedVsActualMinutes}} />`
  - Small stat cards row (like a dashboard header)
- `<RangeToggle selected={"daily"|"weekly"|"monthly"} onSelect={(range) => {}} />`

### Chatbot widget
- `<ChatbotPanel messages={ChatMessage[]} onSend={(text) => {}} activeTaskTitle={string|null} loading={boolean} />`
  - Chat-style UI (message bubbles, user right-aligned / assistant left-aligned), input box + send button
  - Show `activeTaskTitle` at the top ("Chatting about: Guitar practice") when a task context is set, or a generic "General coaching chat" label when not
  - Can be a slide-out panel or floating chat bubble — your call on the exact layout, keep it simple

### Misc
- `<DarkModeToggle isDark={boolean} onToggle={() => {}} />`
- `<GoogleLoginButton onClick={() => {}} />` — simple "Sign in with Google" button (styled per Google's brand guidelines, icon + text)
- `<DueSoonBanner tasks={Task[]} onDismiss={(id) => {}} />` — small banner/toast list for in-app reminders

## Layout
- `<App />` composes: header (logo, dark mode toggle, user avatar), tabs (Tasks / Goals / Analytics), main content area, chatbot panel (toggleable)
- Use dummy sample data to render/test standalone — Part D replaces it with real data

## Deliverable
- One file per component under `src/features/{tasks,goals,analytics,chatbot}/components/`

## Do NOT include
- No `fetch`/`axios`, no API URLs
- No global state (Context/Redux) — Part D owns that
