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

You also need a [setlist.fm API key](https://api.setlist.fm/docs/1.0/index.html) — registration is free.

---

## Configure the API key

```zsh
cp .env.example .env
```

Open `.env` and set:

```
SETLISTFM_API_KEY=your_key
```

---

## Run the backend

```zsh
set -a && source .env && set +a
./mvnw spring-boot:run
```

The backend starts at `http://localhost:8080`.

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
Open `http://localhost:8080/swagger-ui.html` — the Swagger UI loads.

!!! note "Database resets on restart"
    H2 is in-memory. Any users or favorites you create are gone when the backend stops.
