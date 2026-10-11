# Full Project Folder Structure

Hand this to your agent to scaffold the empty folders/files first — then feed each AI's part into the matching location.

```
todo-app/
├── backend/                                   (Spring Boot project)
│   ├── src/main/java/com/todoapp/
│   │   ├── TodoAppApplication.java
│   │   │
│   │   ├── model/                             ← Part A (Qwen)
│   │   │   ├── User.java
│   │   │   ├── Task.java
│   │   │   ├── Recurrence.java                (@Embeddable)
│   │   │   ├── RecurrenceType.java            (enum)
│   │   │   ├── TaskType.java                  (enum)
│   │   │   ├── CompletionLog.java
│   │   │   ├── Goal.java
│   │   │   └── ChatMessage.java
│   │   │
│   │   ├── repository/                        ← Part A (Qwen)
│   │   │   ├── UserRepository.java
│   │   │   ├── TaskRepository.java
│   │   │   ├── CompletionLogRepository.java
│   │   │   ├── GoalRepository.java
│   │   │   └── ChatMessageRepository.java
│   │   │
│   │   ├── dto/                               ← Part B (Gemini) — B1
│   │   │   ├── TaskRequestDTO.java
│   │   │   ├── TaskResponseDTO.java
│   │   │   ├── GoalRequestDTO.java
│   │   │   ├── GoalResponseDTO.java
│   │   │   ├── CompletionLogDTO.java
│   │   │   ├── ChatMessageDTO.java
│   │   │   ├── UserDTO.java
│   │   │   └── mapper/
│   │   │       ├── TaskMapper.java
│   │   │       ├── GoalMapper.java
│   │   │       └── ChatMessageMapper.java
│   │   │
│   │   ├── security/                          ← Part B (Gemini) — B2
│   │   │   ├── OAuth2Config.java
│   │   │   ├── JwtAuthFilter.java
│   │   │   ├── JwtUtil.java
│   │   │   └── SecurityConfig.java
│   │   │
│   │   ├── service/                           ← Part B (Gemini) — B3 + B4
│   │   │   ├── TaskService.java
│   │   │   ├── GoalService.java
│   │   │   ├── AnalyticsService.java
│   │   │   ├── ChatbotService.java
│   │   │   └── PhotoStorageService.java        (Cloudinary)
│   │   │
│   │   ├── controller/                        ← Part B (Gemini) — B3 + B4
│   │   │   ├── AuthController.java
│   │   │   ├── TaskController.java
│   │   │   ├── GoalController.java
│   │   │   ├── AnalyticsController.java
│   │   │   └── ChatbotController.java
│   │   │
│   │   └── exception/                         ← Part B (Gemini)
│   │       ├── TaskNotFoundException.java
│   │       ├── GoalNotFoundException.java
│   │       ├── UnauthorizedException.java
│   │       └── GlobalExceptionHandler.java
│   │
│   ├── src/main/resources/
│   │   └── application.properties             ← Part A (DB) + Part B (OAuth2, JWT secret, Cloudinary, Gemini API keys)
│   │
│   ├── src/test/java/com/todoapp/             (optional, add later)
│   ├── pom.xml
│   └── .env                                   (local secrets, gitignored: DB creds, GEMINI_API_KEY, CLOUDINARY_*, GOOGLE_CLIENT_ID/SECRET, JWT_SECRET)
│
├── frontend/                                   (React + Vite project)
│   ├── src/
│   │   ├── features/
│   │   │   ├── tasks/
│   │   │   │   ├── components/                ← Part C (ChatGPT)
│   │   │   │   │   ├── TaskList.jsx
│   │   │   │   │   ├── TaskItem.jsx
│   │   │   │   │   ├── TaskForm.jsx
│   │   │   │   │   └── CompleteTaskModal.jsx
│   │   │   │   └── hooks/                     ← Part D (Kimi)
│   │   │   │       └── useTasks.js
│   │   │   │
│   │   │   ├── goals/
│   │   │   │   ├── components/                ← Part C (ChatGPT)
│   │   │   │   │   ├── GoalList.jsx
│   │   │   │   │   └── GoalForm.jsx
│   │   │   │   └── hooks/                     ← Part D (Kimi)
│   │   │   │       └── useGoals.js
│   │   │   │
│   │   │   ├── analytics/
│   │   │   │   ├── components/                ← Part C (ChatGPT)
│   │   │   │   │   ├── ProgressBarChart.jsx
│   │   │   │   │   ├── CompletionPieChart.jsx
│   │   │   │   │   ├── SummaryCards.jsx
│   │   │   │   │   └── RangeToggle.jsx
│   │   │   │   └── hooks/                     ← Part D (Kimi)
│   │   │   │       └── useAnalytics.js
│   │   │   │
│   │   │   ├── chatbot/
│   │   │   │   ├── components/                ← Part C (ChatGPT)
│   │   │   │   │   └── ChatbotPanel.jsx
│   │   │   │   └── hooks/                     ← Part D (Kimi)
│   │   │   │       └── useChatbot.js
│   │   │   │
│   │   │   └── auth/
│   │   │       ├── components/                ← Part C (ChatGPT)
│   │   │       │   └── GoogleLoginButton.jsx
│   │   │       └── useAuth.js                 ← Part D (Kimi)
│   │   │
│   │   ├── shared/
│   │   │   ├── api/
│   │   │   │   └── client.js                  ← Part D (Kimi)
│   │   │   ├── components/                    ← Part C (ChatGPT)
│   │   │   │   ├── DarkModeToggle.jsx
│   │   │   │   └── DueSoonBanner.jsx
│   │   │   └── hooks/
│   │   │       └── useDarkMode.js             ← Part D (Kimi)
│   │   │
│   │   ├── App.jsx                             ← Part D (Kimi) wires Part C's components
│   │   ├── main.jsx
│   │   └── index.css                           (Tailwind entry)
│   │
│   ├── .env                                    (VITE_API_URL=http://localhost:8080)
│   ├── index.html
│   ├── package.json
│   ├── tailwind.config.js
│   └── vite.config.js
│
├── API_CONTRACT.md
├── todo-app-build-plan.md
└── README.md
```

## Notes for the agent
- Create `backend/` and `frontend/` as two independent projects (separate `pom.xml` / `package.json`), not a monorepo tool — simplest for a 4GB RAM machine, no extra tooling overhead
- `.env` files go in `.gitignore` in both projects — never commit secrets
- Create empty folders with a `.gitkeep` where there's no file yet, so the structure exists before any AI has generated code into it
- Once scaffolded, drop each part's AI-generated files into the matching folder shown above
