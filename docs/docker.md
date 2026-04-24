# Docker

> Docker Compose runs two services: a PostgreSQL database and the Spring Boot application (which also serves the bundled React frontend).

---

## Run with Docker Compose

```zsh
cp .env.example .env   # set SETLISTFM_API_KEY, DB_USERNAME, DB_PASSWORD
docker compose up --build
```

Open `http://localhost:8080` — frontend and API both served from there.

Compose waits for PostgreSQL to pass its healthcheck before starting the backend. Flyway runs migrations automatically on first boot.

---

## Build the image manually

```zsh
docker build -t stagefinder .
docker run -p 8080:8080 \
  -e SETLISTFM_API_KEY=your_key \
  -e DB_URL=jdbc:postgresql://host:5432/stagefinder \
  -e DB_USERNAME=stagefinder \
  -e DB_PASSWORD=stagefinder \
  stagefinder
```

When running the image standalone, point `DB_URL` at an external PostgreSQL instance.

---

## What the build does

1. Node stage: `npm ci && npm run build` inside `frontend/` → `dist/`
2. Maven stage: `./mvnw package -DskipTests` → fat jar
3. The Maven stage copies `frontend/dist` into `src/main/resources/static/` before packaging, so Spring Boot serves the frontend as static files
4. Final image: `eclipse-temurin:21-jre-alpine` + the fat jar

---

## Environment variables

Passed at runtime via `-e` or in `docker-compose.yml`. See [Environment variables](environment.md) for the full list.
