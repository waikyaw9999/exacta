# Exacta

Time tracking and billing for law firms and hourly consultants. The timer stays on screen so billable work is not lost, and the dashboard shows unbilled revenue in real time.

## Quick start (recommended)

Docker Desktop is enough. You do not need Java or Node installed.

```bash
copy .env.example .env
docker compose up --build
```

Open [http://localhost:3000](http://localhost:3000) for the landing page, then sign in at `/login`:

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

### Automatic deploy to a z.com (or any) VPS

z.com does not connect to GitHub in the dashboard. The VM is just Ubuntu. GitHub Actions SSHs in after it pushes images.

**One-time on the server** (Ubuntu, as a user in the `docker` group):

```bash
sudo apt update
sudo apt install -y git docker.io docker-compose-v2
sudo usermod -aG docker $USER
# log out and back in

git clone https://github.com/<your-user>/exacta.git /opt/exacta
cd /opt/exacta
cp .env.example .env
nano .env   # set POSTGRES_PASSWORD, JWT_SECRET, CORS_ALLOWED_ORIGINS=http://YOUR_SERVER_IP
docker compose -f docker-compose.prod.yml up -d
```

Create a **separate SSH key** for GitHub (do not reuse your laptop key):

```bash
ssh-keygen -t ed25519 -C "github-deploy" -f github-deploy -N ""
```

Add `github-deploy.pub` to the server (`~/.ssh/authorized_keys`). Keep `github-deploy` private.

**GitHub → Settings → Secrets and variables → Actions**

Secrets:

| Secret | Value |
| --- | --- |
| `SSH_HOST` | VPS public IP |
| `SSH_USER` | Ubuntu username (often `ubuntu` or `root`) |
| `SSH_PRIVATE_KEY` | Full contents of the **private** `github-deploy` file |
| `DEPLOY_PATH` | `/opt/exacta` |

Variable (Settings → Secrets and variables → Actions → **Variables**):

| Variable | Value |
| --- | --- |
| `ENABLE_VPS_DEPLOY` | `true` |

Security group: allow **22**, **80**, and **443**. Do not open **5432**.

After that, every push to `main` tests, pushes Docker images, then SSHs to the VM and runs `docker compose pull && up -d`. Leave `ENABLE_VPS_DEPLOY` unset until the VM is ready so the workflow does not fail on SSH.

## API

| Method | Path | Notes |
| --- | --- | --- |
| POST | `/api/v1/auth/register` | First user on an empty DB is ADMIN |
| POST | `/api/v1/auth/login` | Returns JWT |
| GET | `/api/v1/dashboard` | Unbilled revenue + weekly hours + recent entries |
| GET/POST | `/api/v1/time-entries` | CRUD; POST on timer stop |
| GET | `/api/v1/clients` | Auth required |
| GET | `/api/v1/projects` | Auth required |
