# Stagefinder

> Search for artists via setlist.fm, browse their setlists, and build a personal watchlist.

---

## Quickstart

```zsh
cp .env.example .env
# set SETLISTFM_API_KEY in .env

set -a && source .env && set +a
./mvnw spring-boot:run
```

In a second terminal:

```zsh
cd frontend
npm install
npm run dev
```

| Service  | URL                       |
|----------|---------------------------|
| Frontend | http://localhost:5173      |
| Backend  | http://localhost:8080      |
| Swagger  | http://localhost:8080/swagger-ui.html |

Or run everything in one container:

```zsh
docker compose up --build
# → http://localhost:8080
```

---

## What it does

- Search artists by name (proxied through the backend to setlist.fm)
- Browse past setlists per artist — date, venue, city, song list
- Save artists as favorites, with an optional personal note
- Manage a user account (no authentication — dev-mode only)