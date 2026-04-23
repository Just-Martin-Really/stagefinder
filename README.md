# Stagefinder

Discover where your favourite artists are playing. Search for artists via the **setlist.fm** API, browse past and upcoming setlists, and build a personal watchlist of favourites.

---

## Architecture

```
┌──────────────────┐   HTTP/JSON   ┌─────────────────────┐   REST   ┌────────────────┐
│  Frontend        │ ◄───────────► │  Backend             │ ◄──────► │  setlist.fm    │
│  React + Vite    │               │  Spring Boot (Java)  │          │  External API  │
│  :5173 (dev)     │               │  :8080               │          └────────────────┘
└──────────────────┘               │                      │
                                   │  Spring Data JPA     │
                                   │  H2 (in-memory)      │
                                   └─────────────────────┘
```

- **Frontend** communicates only with the backend — no direct calls to setlist.fm.
- **Backend** owns all business logic, persistence, and external API integration.
- **Database** is H2 in-memory for development (resets on restart).

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot 4, Maven |
| Persistence | Spring Data JPA, H2 |
| Validation | Bean Validation (`@Valid`) |
| API Docs | springdoc-openapi (Swagger UI) |
| Frontend | React 19, Vite, React Router |
| External API | [setlist.fm REST API v1.0](https://api.setlist.fm/docs/1.0/index.html) |

---

## Domain Model

```
User ──< Favorite >── Artist
```

- **User** — registered account (username, email)
- **Artist** — cached from setlist.fm (mbid, name, sortName, url)
- **Favorite** — join between User and Artist, with an optional personal note

---

## Prerequisites

- Java 21+
- Maven (or use the included `./mvnw`)
- Node.js 18+ and npm
- A [setlist.fm API key](https://api.setlist.fm/docs/1.0/index.html) (free registration)

---

## Local Setup

### 1. API key

```zsh
cp .env.example .env
# Edit .env and set SETLISTFM_API_KEY=your_key_here
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

Frontend starts at **http://localhost:5173**

---

## API Documentation (Swagger UI)

With the backend running, open:

```
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON spec:

```
http://localhost:8080/v3/api-docs
```

---

## REST API Overview

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/users` | List all users |
| `POST` | `/api/users` | Create a user |
| `GET` | `/api/users/{id}` | Get user by ID |
| `PUT` | `/api/users/{id}` | Update user |
| `DELETE` | `/api/users/{id}` | Delete user |
| `GET` | `/api/users/{userId}/favorites` | List user's favorites |
| `POST` | `/api/users/{userId}/favorites` | Add favorite (resolves artist from setlist.fm) |
| `PATCH` | `/api/users/{userId}/favorites/{id}` | Update favorite note |
| `DELETE` | `/api/users/{userId}/favorites/{id}` | Remove favorite |
| `GET` | `/api/artists` | List locally cached artists |
| `GET` | `/api/artists/{id}` | Get artist by local ID |
| `GET` | `/api/setlists/search?q=` | Search artists on setlist.fm |
| `GET` | `/api/setlists/{mbid}` | Get setlists for an artist |

---

## Running Tests

```zsh
set -a && source .env && set +a
./mvnw test
```

**29 tests** across three layers:

| Layer | Classes | Tests |
|---|---|---|
| Unit (service) | `UserServiceTest`, `FavoriteServiceTest`, `ArtistServiceTest` | 14 |
| Unit (adapter) | `SetlistFmServiceTest` | 4 |
| Integration | `UserControllerIT`, `FavoriteControllerIT` | 10 |
| Smoke | `StagefinderApplicationTests` | 1 |

The `SetlistFmLiveIntegrationTest` is opt-in and skipped by default:

```zsh
RUN_SETLISTFM_LIVE_TESTS=true ./mvnw -Dtest=SetlistFmLiveIntegrationTest test
```

---

## Docker

Build and start the backend:

```zsh
cp .env.example .env   # set SETLISTFM_API_KEY
docker compose up --build
```

Backend will be available at **http://localhost:8080**.  
Run the frontend separately with `npm run dev` in the `frontend/` directory.

---

## Environment Variables

| Variable | Default | Description |
|---|---|---|
| `SETLISTFM_API_KEY` | _(required)_ | Your setlist.fm API key |
| `SETLISTFM_BASE_URL` | `https://api.setlist.fm` | Base URL for setlist.fm |
| `SETLISTFM_API_VERSION` | `1.0` | API version path segment |
| `SETLISTFM_TIMEOUT_SECONDS` | `10` | HTTP timeout for external calls |

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
│   ├── config/                 # CORS config
│   ├── domain/
│   │   ├── entity/             # JPA entities (User, Artist, Favorite)
│   │   └── repository/         # Spring Data repositories
│   └── service/                # Business logic
├── src/test/                   # Unit + integration tests
├── frontend/                   # React + Vite frontend
│   └── src/
│       ├── api/                # Typed fetch client
│       ├── components/         # Reusable UI components
│       └── pages/              # Route-level views
├── Dockerfile
├── docker-compose.yml
└── .github/workflows/ci.yml    # GitHub Actions CI
```

---

## Known Limitations

- H2 database is in-memory — data is lost on restart. Suitable for development only.
- No authentication — any client can read and write any user's data.
- Artist data is fetched from setlist.fm on demand and cached locally by mbid.
