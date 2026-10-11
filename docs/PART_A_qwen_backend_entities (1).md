# Part A — Backend: Entities + DB Layer
**Tool: Qwen**
**Prerequisite: paste `API_CONTRACT.md` first, then this file.**

## Scope
Build ONLY the JPA entity layer, repositories, and DB config. No controllers/services (Part B).

## What to generate

### 1. Enums
- `TaskType` — `HABIT, TASK, SKILL, HEALTH`
- `RecurrenceType` — `NONE, DAILY, WEEKLY, CUSTOM_DAYS, FOR_N_DAYS`
- `DayOfWeek` — reuse Java's built-in `java.time.DayOfWeek`, don't create a custom one

### 2. Entity: `User`
- `id`, `googleId` (unique), `email` (unique), `name`, `avatarUrl`, `personalityNotes` (TEXT column, nullable)

### 3. Entity: `Task`
Matches the contract's `Task` model:
- `@ManyToOne` to `User`
- `type`, `@Enumerated(EnumType.STRING)`
- `priority`: `int`, add a `@Min(1) @Max(5)` validation annotation
- `@ManyToOne` to `Goal`, nullable
- `dueDate` (`LocalDate`, nullable), `dueTime` (`LocalTime`, nullable)
- `expectedDurationMinutes`: `Integer`, nullable
- `reminderDateTime`: `LocalDateTime`, nullable
- **Recurrence**: embed as `@Embeddable` class `Recurrence` with fields `type` (enum), `daysOfWeek` (`@ElementCollection` of `DayOfWeek`), `occurrences` (`Integer`, nullable), `endDate` (`LocalDate`, nullable). Embed it in `Task` via `@Embedded`.
- `archived` (boolean, default false), `position` (int, default 0)
- `createdAt`/`updatedAt`: `@CreationTimestamp`/`@UpdateTimestamp`

### 4. Entity: `CompletionLog`
- `@ManyToOne` to `Task`
- `completedAt` (`LocalDateTime`, `@CreationTimestamp` is fine since it's always "now" when created)
- `actualDurationMinutes` (`Integer`, nullable)
- `photoUrl` (`String`, nullable)
- `note` (`String`, nullable)

### 5. Entity: `Goal`
- `@ManyToOne` to `User`
- `title`, `type` (enum, same `TaskType`), `targetDate` (`LocalDate`, nullable)
- `@OneToMany(mappedBy = "goal")` back-reference to `Task` (this gives you `linkedTaskIds` by mapping `task.getId()` over the list — do that mapping in Part B, not here)

### 6. Entity: `ChatMessage`
- `@ManyToOne` to `User`, `@ManyToOne` to `Task` (nullable)
- `role` (`String` or enum `"user"/"assistant"`), `content` (`@Column(columnDefinition = "TEXT")`), `createdAt` (`@CreationTimestamp`)

### 7. Repositories
- `UserRepository` — add `Optional<User> findByGoogleId(String googleId)`
- `TaskRepository` — add `findByUserIdAndArchived(Long userId, boolean archived)`, `findByUserIdAndType(Long userId, TaskType type)`, `findByUserIdAndReminderDateTimeBefore(Long userId, LocalDateTime time)` (for due-soon)
- `CompletionLogRepository` — add `findByTaskId(Long taskId)`, and a method to find logs by user within a date range (join through Task) for analytics — e.g. `findByTask_UserIdAndCompletedAtBetween(Long userId, LocalDateTime start, LocalDateTime end)`
- `GoalRepository` — add `findByUserId(Long userId)`
- `ChatMessageRepository` — add `findByUserIdAndTaskIdOrderByCreatedAtAsc(Long userId, Long taskId)`

### 8. DB config
- PostgreSQL (not H2 this time, since we need a real persistent DB for multi-user + auth). Use a free-tier hosted Postgres (Neon, Supabase, or Railway — any is fine) or local Postgres via Docker if the user prefers local dev.
- `application.properties`:
  ```
  spring.datasource.url=jdbc:postgresql://<host>:5432/<db>
  spring.datasource.username=<user>
  spring.datasource.password=<password>
  spring.jpa.hibernate.ddl-auto=update
  spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
  ```

## Deliverable
- `model/` package: `User.java`, `Task.java`, `Recurrence.java` (embeddable), `CompletionLog.java`, `Goal.java`, `ChatMessage.java`, `TaskType.java`, `RecurrenceType.java`
- `repository/` package: all five repositories listed above
- `application.properties`

## Do NOT include
- No controllers, services, DTOs, or security config (Part B handles those)
