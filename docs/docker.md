# Docker

> Single-container setup — the React frontend is bundled into the Spring Boot jar at build time.

---

## Run with Docker Compose

```zsh
cp .env.example .env   # set SETLISTFM_API_KEY
docker compose up --build
```

Open `http://localhost:8080` — frontend and API both served from there.

---

## Build the image manually

```zsh
docker build -t stagefinder .
docker run -p 8080:8080 -e SETLISTFM_API_KEY=your_key stagefinder
```

---

## What the build does

1. Node stage: `npm ci && npm run build` inside `frontend/` → `dist/`
2. Maven stage: `./mvnw package -DskipTests` → fat jar
3. The Maven stage copies `frontend/dist` into `src/main/resources/static/` before packaging, so Spring Boot serves the frontend as static files
4. Final image: `eclipse-temurin:21-jre-alpine` + the fat jar

---

## Environment variables

Passed at runtime via `-e` or in `docker-compose.yml`. See the full list on the [environment variables](environment.md) page.
