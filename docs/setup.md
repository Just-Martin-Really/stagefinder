# Local setup

> Get the backend and frontend running on your machine.

---

## Prerequisites

| Tool | Version |
|------|---------|
| Java | 21+ |
| Maven | any (or use `./mvnw`) |
| Node.js | 18+ |
| npm | bundled with Node |
| PostgreSQL | 15+ |

You also need a [setlist.fm API key](https://api.setlist.fm/docs/1.0/index.html) — registration is free.

---

## Start PostgreSQL

The application requires a running PostgreSQL instance. With Docker:

```zsh
docker run -d \
  --name stagefinder-pg \
  -e POSTGRES_DB=stagefinder \
  -e POSTGRES_USER=stagefinder \
  -e POSTGRES_PASSWORD=stagefinder \
  -p 5432:5432 \
  postgres:17-alpine
```

Or use an existing local PostgreSQL — create a database and user that match your `.env` values.

---

## Configure environment variables

```zsh
cp .env.example .env
```

Open `.env` and set at minimum:

```
SETLISTFM_API_KEY=your_key
DB_USERNAME=stagefinder
DB_PASSWORD=stagefinder
```

See [Environment variables](environment.md) for the full list.

---

## Run the backend

```zsh
set -a && source .env && set +a
./mvnw spring-boot:run
```

Flyway runs the schema migrations automatically on startup. The backend starts at `http://localhost:8080`.

---

## Run the frontend

```zsh
cd frontend
npm install
npm run dev
```

The frontend starts at `http://localhost:5173` and proxies API calls to `:8080`.

---

## Verify

Open `http://localhost:5173` — the search page loads.  
Open `http://localhost:8080/swagger-ui.html` — the Swagger UI loads with all endpoints.
