# FocusFlow Architecture

```mermaid
flowchart LR
    User[User]
    Browser[React + Vite frontend]
    Api[Spring Boot REST API]
    Security[Spring Security<br/>Google OAuth 2.0 + JWT]
    Controllers[REST controllers]
    Services[Application services]
    Repositories[Spring Data JPA repositories]
    Database[(PostgreSQL)]
    Google[Google OAuth]
    Groq[Groq API<br/>Qwen 3.8 27B]
    Cloudinary[Cloudinary]

    User --> Browser
    Browser -->|OAuth sign-in| Api
    Api <-->|Authorization flow| Google
    Api -->|JWT redirect| Browser
    Browser -->|Authenticated REST requests| Api
    Api --> Security
    Security --> Controllers
    Controllers --> Services
    Services --> Repositories
    Repositories --> Database
    Services -->|AI coaching requests| Groq
    Services -->|Completion photo storage| Cloudinary
```

## Request flow

- Google OAuth authenticates a user through the backend. On success, the backend issues a JWT to the frontend.
- The frontend sends the JWT with requests to protected `/api/**` endpoints.
- Controllers delegate application operations to services. Services use repositories to read and write PostgreSQL data.
- The chatbot service calls Groq for AI responses, while photo uploads are stored with Cloudinary.
