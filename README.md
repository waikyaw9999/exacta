# Exacta

Time tracking and billing for law firms and hourly consultants. The timer stays on screen so billable work is not lost, and the dashboard shows unbilled revenue in real time.

## Quick start (recommended)

Docker Desktop is enough. You do not need Java or Node installed.

```bash
copy .env.example .env
docker compose up --build
```

Open [http://localhost:3000](http://localhost:3000) and sign in:

| Role | Email | Password |
| --- | --- | --- |
| Admin | `ada@exacta.test` | `ExactaDemo1!` |
| Member | `marcus@exacta.test` | `ExactaDemo1!` |

The first launch seeds clients, projects, and a week of time entries. Admin sees firm-wide analytics; members see their own.

To reset demo data:

```bash
docker compose down -v
docker compose up --build
```

## What works

- JWT login and registration
- Persistent floating timer (start/stop saves to `/api/v1/time-entries`)
- Live dashboard: unbilled revenue, billable vs non-billable hours this week, recent entries
- PostgreSQL schema via Flyway

## Local development

**API** (Java 17 + Maven) with Postgres from Compose:

```bash
docker compose up db -d
cd backend
mvn spring-boot:run
```

**UI** (Node 18+):

```bash
cd frontend
npm ci
npm run dev
```

Vite is on `http://localhost:5173` and proxies `/api` to `http://localhost:8080`.

## Deploy

Production is the same Compose stack, with a strong `JWT_SECRET` and database password in `.env`. Point a reverse proxy at port `3000` (or set `FRONTEND_PORT`).

```bash
APP_SEED_ENABLED=false
JWT_SECRET=<at-least-32-characters>
POSTGRES_PASSWORD=<strong-password>
```

Set `APP_SEED_ENABLED=false` once you no longer want demo users created on an empty database.

GitHub Actions builds the backend and frontend on every push. Images publish to Docker Hub on `main` when `DOCKER_USERNAME` and `DOCKER_PASSWORD` secrets are configured.

## API

| Method | Path | Notes |
| --- | --- | --- |
| POST | `/api/v1/auth/register` | First user on an empty DB is ADMIN |
| POST | `/api/v1/auth/login` | Returns JWT |
| GET | `/api/v1/dashboard` | Unbilled revenue + weekly hours + recent entries |
| GET/POST | `/api/v1/time-entries` | CRUD; POST on timer stop |
| GET | `/api/v1/clients` | Auth required |
| GET | `/api/v1/projects` | Auth required |
