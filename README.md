# Stagefinder

**Docs:** https://just-martin-really.github.io/stagefinder/

Discover where your favourite artists are playing. Search for artists via the **setlist.fm** API, browse past setlists, and build a personal watchlist of favourites.

---

## Architecture

```
┌──────────────────┐   HTTP/JSON   ┌──────────────────────┐   REST   ┌────────────────┐
│  Frontend        │ ◄───────────► │  Backend             │ ◄──────► │  setlist.fm    │
│  React + Vite    │               │  Spring Boot (Java)  │          │  External API  │
│  :5173 (dev)     │               │  :8080               │          └────────────────┘
└──────────────────┘               │                      │
                                   │  Spring Data JPA     │
                                   │  PostgreSQL 17       │
                                   └──────────────────────┘
```

- **Frontend** communicates only with the backend — no direct calls to setlist.fm.
- **Backend** owns all business logic, persistence, auth, and external API integration.
- **Database** is PostgreSQL 17, managed by Flyway migrations, running via Docker Compose.

---

## Tech Stack

| Layer        | Technology                                                             |
|--------------|------------------------------------------------------------------------|
| Backend      | Java 21, Spring Boot 4, Maven                                          |
| Persistence  | Spring Data JPA, PostgreSQL 17, Flyway                                 |
| Auth         | Spring Security, BCrypt, Spring Session JDBC                           |
| Validation   | Bean Validation (`@Valid`)                                             |
| API Docs     | springdoc-openapi (Swagger UI)                                         |
| Frontend     | React 19, Vite, React Router                                           |
| External API | [setlist.fm REST API v1.0](https://api.setlist.fm/docs/1.0/index.html) |

---

## Domain Model

```
User ──< Favorite >── Artist
```

- **User** — registered account (username, email, BCrypt password hash); owns their favorites
- **Artist** — cached from setlist.fm (mbid, name, sortName, url)
- **Favorite** — join between User and Artist, with an optional personal note

---

## Prerequisites

- Docker and Docker Compose (recommended — bundles everything)
- Or for local dev: Java 21+, Maven, Node.js 18+, PostgreSQL 17
- A [setlist.fm API key](https://api.setlist.fm/docs/1.0/index.html) (free registration)

---

## Run with Docker

The Docker build compiles the React frontend and bundles it into the Spring Boot jar — one container serves everything alongside a PostgreSQL container.

```zsh
cp .env.example .env   # set SETLISTFM_API_KEY
docker compose up --build
```

Open **http://localhost:8080** — frontend and API both served from there.

---

## Local Development Setup

### 1. Configure environment

```zsh
cp .env.example .env
# Set SETLISTFM_API_KEY and DB_* vars to point at a local PostgreSQL instance
```

### 2. Run the backend

```zsh
set -a && source .env && set +a
./mvnw spring-boot:run
```

Backend starts at **http://localhost:8080**

### 3. Run the frontend

```zsh
cd frontend
npm install
npm run dev
```

Frontend starts at **http://localhost:5173** and proxies `/api` to `:8080`.

---

## API Documentation

With the backend running, open Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON spec: `http://localhost:8080/v3/api-docs`

---

## REST API Overview

| Method   | Path                                 | Auth     | Description                                    |
|----------|--------------------------------------|----------|------------------------------------------------|
| `POST`   | `/api/auth/login`                    | —        | Log in, receive session cookie                 |
| `GET`    | `/api/auth/me`                       | required | Return current session user                    |
| `POST`   | `/api/auth/logout`                   | required | Invalidate session                             |
| `POST`   | `/api/users`                         | —        | Register a new account                         |
| `GET`    | `/api/users/{id}`                    | required | Get user by ID                                 |
| `PUT`    | `/api/users/{id}`                    | required | Update account (owner only)                    |
| `DELETE` | `/api/users/{id}`                    | required | Delete account (owner only)                    |
| `GET`    | `/api/users/{userId}/favorites`      | required | List user's favorites                          |
| `POST`   | `/api/users/{userId}/favorites`      | required | Add favorite (resolves artist from setlist.fm) |
| `PATCH`  | `/api/users/{userId}/favorites/{id}` | required | Update favorite note                           |
| `DELETE` | `/api/users/{userId}/favorites/{id}` | required | Remove favorite                                |
| `GET`    | `/api/setlists/search?q=`            | —        | Search artists on setlist.fm                   |
| `GET`    | `/api/setlists/{mbid}`               | —        | Get setlists for an artist                     |

---

## Running Tests

```zsh
set -a && source .env && set +a
./mvnw test
```

**47 tests** across four layers:

| Layer          | Classes                                                                        | Tests |
|----------------|--------------------------------------------------------------------------------|-------|
| Unit (service) | `UserServiceTest`, `FavoriteServiceTest`, `ArtistServiceTest`                  | 15    |
| Unit (adapter) | `SetlistFmServiceTest`                                                         | 4     |
| Integration    | `UserControllerIT`, `FavoriteControllerIT`, `AuthControllerIT`, `SetlistControllerIT` | 27 |
| Smoke          | `StagefinderApplicationTests`                                                  | 1     |

The `SetlistFmLiveIntegrationTest` is opt-in and skipped by default:

```zsh
RUN_SETLISTFM_LIVE_TESTS=true ./mvnw -Dtest=SetlistFmLiveIntegrationTest test
```

---

## Environment Variables

| Variable                    | Default                  | Description                     |
|-----------------------------|--------------------------|---------------------------------|
| `SETLISTFM_API_KEY`         | _(required)_             | Your setlist.fm API key         |
| `SETLISTFM_BASE_URL`        | `https://api.setlist.fm` | Base URL for setlist.fm         |
| `SETLISTFM_API_VERSION`     | `1.0`                    | API version path segment        |
| `SETLISTFM_TIMEOUT_SECONDS` | `10`                     | HTTP timeout for external calls |
| `DB_URL`                    | `jdbc:postgresql://localhost:5432/stagefinder` | JDBC connection URL |
| `DB_USERNAME`               | `stagefinder`            | PostgreSQL username             |
| `DB_PASSWORD`               | `stagefinder`            | PostgreSQL password             |

---

## Project Structure

```
stagefinder/
├── src/main/java/.../stagefinder/
│   ├── adapter/setlistfm/      # setlist.fm HTTP client + response models
│   ├── api/
│   │   ├── controller/         # REST controllers
│   │   ├── dto/                # Request/response DTOs
│   │   └── exception/          # GlobalExceptionHandler + typed exceptions
│   ├── config/                 # SecurityConfig (auth rules, CORS, session policy)
│   ├── domain/
│   │   ├── entity/             # JPA entities (User, Artist, Favorite)
│   │   └── repository/         # Spring Data repositories
│   └── service/                # Business logic
├── src/main/resources/
│   └── db/migration/           # Flyway SQL migrations
├── src/test/                   # Unit + integration tests
├── frontend/                   # React + Vite frontend
│   └── src/
│       ├── api/                # Typed fetch client
│       ├── components/         # Reusable UI components (Hero, AuthModal)
│       └── pages/              # Route-level views
├── docs/                       # MkDocs documentation source
├── Dockerfile
├── docker-compose.yml
└── .github/workflows/ci.yml    # GitHub Actions CI
```
