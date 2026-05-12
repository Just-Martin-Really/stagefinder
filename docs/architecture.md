# Architecture

> Three-layer system: React frontend → Spring Boot backend → setlist.fm external API.

---

## System diagram

```mermaid
flowchart LR
    F["**Frontend**\nReact + Vite\n:5173 (dev)"]
    B["**Backend**\nSpring Boot\n:8080"]
    D[("PostgreSQL 17\nDocker Compose")]
    S["**setlist.fm**\nExternal API"]

    F <-->|HTTP/JSON| B
    B <-->|REST| S
    B --- D
```

The frontend never calls setlist.fm directly. All external traffic goes through the backend.

---

## Backend layers

| Package | Role |
|---------|------|
| `api/controller` | REST controllers — maps HTTP to service calls |
| `api/dto` | Request and response shapes — validated with Bean Validation |
| `api/exception` | `GlobalExceptionHandler` + typed exceptions (`NotFoundException`, `ConflictException`, `ExternalServiceException`) |
| `service` | Business logic — `UserService`, `FavoriteService`, `ArtistService` |
| `domain/entity` | JPA entities — `User`, `Artist`, `Favorite` |
| `domain/repository` | Spring Data repositories |
| `adapter/setlistfm` | HTTP client for setlist.fm, response models, config |
| `config` | `SecurityConfig` (auth rules, CORS, session policy), `CacheConfig` (Caffeine cache for setlist.fm responses) |

---

## Domain model

```
User ──< Favorite >── Artist
```

- **User** — username, email, BCrypt password hash; owns their favorites (mutations are ownership-guarded)
- **Artist** — fetched from setlist.fm on first request, then persisted locally and looked up by `mbid` on subsequent requests
- **Favorite** — join between one user and one artist, with an optional note and a creation timestamp

The `(user_id, artist_id)` pair has a unique constraint — a user can only favorite an artist once.

---

## Caching

setlist.fm responses are cached in-process with Caffeine. Configured in `CacheConfig`:

| Cache | Methods | TTL | Max entries |
|-------|---------|-----|-------------|
| `setlistfm-artist` | `SetlistFmService.getArtist(mbid)` | 24h | 1000 |
| `setlistfm-setlists` | `SetlistFmService.getSetlists(mbid, page)` | 15min | 5000 |

`searchArtists` is intentionally not cached — query keyspace is too large and hit rate too low. The cache is gated on `spring.cache.type=caffeine`, which is the production default; the test profile sets `none` so mock stubs are not shadowed by cached results.

---

## Tech stack

| Layer | Technology |
|-------|------------|
| Backend | Java 21, Spring Boot 4, Maven |
| Persistence | Spring Data JPA, PostgreSQL 17, Flyway |
| Auth | Spring Security, BCrypt, Spring Session JDBC |
| Validation | Bean Validation (`@Valid`) |
| Cache | Spring Cache abstraction + Caffeine (in-memory) |
| API docs | springdoc-openapi (Swagger UI) |
| Frontend | React 19, Vite, React Router |
| External API | setlist.fm REST API v1.0 |