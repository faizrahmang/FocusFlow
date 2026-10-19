# FocusFlow

FocusFlow is a task, habit, goal, and skill-tracking app with progress analytics and an AI coaching chat. It has a React/Vite frontend and a Spring Boot API backed by PostgreSQL.

## Features

- Sign in with Google OAuth 2.0
- Create, organize, schedule, and complete tasks, habits, skills, and health activities
- Set goals and track completion history
- View progress analytics and upcoming reminders
- Chat with an AI coach about general goals or a specific task
- Use light or dark mode

## Tech stack

- Frontend: React 19, Vite, Tailwind CSS, Recharts, Axios
- Backend: Java 21, Spring Boot 3.4, Spring Data JPA, Spring Security
- Database: PostgreSQL
- Integrations: Google OAuth 2.0, Gemini API, Cloudinary

## Prerequisites

- Java 21
- Maven
- Node.js and npm
- A PostgreSQL database
- Google OAuth client credentials
- Cloudinary credentials
- A Gemini API key for AI coaching

## Configuration

The backend reads configuration from environment variables. Set these before starting it:

| Variable | Purpose |
| --- | --- |
| `DB_URL` | PostgreSQL JDBC URL, for example `jdbc:postgresql://localhost:5432/focusflow` |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `GOOGLE_CLIENT_ID` | Google OAuth client ID |
| `GOOGLE_CLIENT_SECRET` | Google OAuth client secret |
| `JWT_SECRET` | Secret used to sign authentication tokens |
| `CLOUDINARY_CLOUD_NAME` | Cloudinary cloud name |
| `CLOUDINARY_API_KEY` | Cloudinary API key |
| `CLOUDINARY_API_SECRET` | Cloudinary API secret |
| `GEMINI_API_KEY` | Gemini API key used for AI coaching |

Keep credentials out of source control. For local development, configure them in your shell or in a local environment file that is excluded from Git.

The frontend defaults to `http://localhost:8080` for the API. To use another backend URL, set `VITE_API_URL` in `frontend/.env`, for example:

```dotenv
VITE_API_URL=http://localhost:8080
```

The backend uses port `8080` and redirects successful Google sign-ins to `http://localhost:5173`. Update the corresponding backend configuration if your local ports differ.

## Run locally

Start the backend from the repository root:

```powershell
Set-Location backend
mvn spring-boot:run
```

In another terminal, install frontend dependencies and start the Vite development server:

```powershell
Set-Location frontend
npm install
npm run dev
```

Open the frontend URL printed by Vite (by default `http://localhost:5173`). The API requires authentication for `/api/**` endpoints.

## Build

Build the frontend:

```powershell
Set-Location frontend
npm run build
```

Run backend tests:

```powershell
Set-Location backend
mvn test
```

## Documentation

- [API contract](docs/API_CONTRACT.md)
- [Project structure](docs/PROJECT_FOLDER_STRUCTURE.md)
