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
- Integrations: Google OAuth 2.0, Groq API (Qwen), Cloudinary

## Prerequisites

- Java 21
- Maven
- Node.js and npm
- A PostgreSQL database
- Google OAuth client credentials
- Cloudinary credentials
- A Groq API key for AI coaching

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
| `FRONTEND_URL` | Frontend origin allowed by CORS and used after Google sign-in |
| `CLOUDINARY_CLOUD_NAME` | Cloudinary cloud name |
| `CLOUDINARY_API_KEY` | Cloudinary API key |
| `CLOUDINARY_API_SECRET` | Cloudinary API secret |
| `GROQ_API_KEY` | Groq API key used for AI coaching |

Keep credentials out of source control. For local development, configure them in your shell or in a local environment file that is excluded from Git.

The frontend defaults to `http://localhost:8080` for the API. To use another backend URL, set `VITE_API_URL` in `frontend/.env`, for example:

```dotenv
VITE_API_URL=http://localhost:8080
```

The backend defaults to port `8080` and redirects successful Google sign-ins to `http://localhost:5173`. Set `FRONTEND_URL` to the deployed frontend's exact origin in production; the same origin is allowed by CORS. The backend honors the hosting provider's `PORT` environment variable.

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

## Deploy on Render

The backend includes a Java 21 Dockerfile at `backend/Dockerfile`. Create a Render Web Service connected to this repository, choose Docker, set its root directory to `backend`, and set its Dockerfile path to `Dockerfile`. Configure the backend environment variables listed above in the Render dashboard. After deploying the frontend, set `FRONTEND_URL` to its exact HTTPS origin and redeploy the backend.

## Architecture

See the [architecture diagram](ARCHITECTURE.md) for the application components and their interactions.
