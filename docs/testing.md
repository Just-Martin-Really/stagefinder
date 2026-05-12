# Testing

> 64 tests across five layers — unit, adapter, DTO, integration, and smoke.

---

## Run the test suite

```zsh
set -a && source .env && set +a
./mvnw test
```

---

## Test coverage

| Layer | Classes | Tests |
|-------|---------|-------|
| Unit — service | `UserServiceTest`, `FavoriteServiceTest`, `ArtistServiceTest`, `FeedServiceTest`, `ArtistStatsServiceTest` | 28 |
| Unit — adapter | `SetlistFmServiceTest`, `SetlistFmCacheTest` | 7 |
| Unit — DTO | `PasswordToStringTest` | 3 |
| Integration | `AuthControllerIT`, `UserControllerIT`, `FavoriteControllerIT`, `SetlistControllerIT` | 24 |
| Smoke | `StagefinderApplicationTests` | 1 |
| Live (skipped) | `SetlistFmLiveIntegrationTest` | 1 |
| **Total** | | **64** |

---

## Cache test isolation

The test profile sets `spring.cache.type=none` in `src/test/resources/application.properties`, so `@Cacheable` calls on `SetlistFmService` are no-ops in every test by default. Cached results never leak between tests.

`SetlistFmCacheTest` re-enables Caffeine via `@TestPropertySource(properties = "spring.cache.type=caffeine")` and verifies cache hits with Mockito's `verify(client, times(1))`.

---

## Live integration test

`SetlistFmLiveIntegrationTest` calls the real setlist.fm API. It is skipped by default.

```zsh
RUN_SETLISTFM_LIVE_TESTS=true ./mvnw -Dtest=SetlistFmLiveIntegrationTest test
```

!!! warning "Requires a valid API key"
    Set `SETLISTFM_API_KEY` in your environment before running the live test.

---

## Run a single test class

```zsh
./mvnw -Dtest=UserServiceTest test
```
