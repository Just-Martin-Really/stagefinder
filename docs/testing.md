# Testing

> 29 tests across four layers — unit, adapter, integration, and smoke.

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
| Unit — service | `UserServiceTest`, `FavoriteServiceTest`, `ArtistServiceTest` | 14 |
| Unit — adapter | `SetlistFmServiceTest` | 4 |
| Integration | `UserControllerIT`, `FavoriteControllerIT` | 10 |
| Smoke | `StagefinderApplicationTests` | 1 |
| **Total** | | **29** |

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
