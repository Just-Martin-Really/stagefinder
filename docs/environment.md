# Environment variables

> All configuration is read from environment variables — copy `.env.example` to `.env` to get started.

---

## Variables

### Database

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/stagefinder` | JDBC connection URL |
| `DB_USERNAME` | `stagefinder` | Database user |
| `DB_PASSWORD` | `stagefinder` | Database password |

### setlist.fm

| Variable | Default | Description |
|----------|---------|-------------|
| `SETLISTFM_API_KEY` | _(required)_ | Your setlist.fm API key |
| `SETLISTFM_BASE_URL` | `https://api.setlist.fm` | Base URL for setlist.fm |
| `SETLISTFM_API_VERSION` | `1.0` | API version path segment |
| `SETLISTFM_TIMEOUT_SECONDS` | `10` | HTTP timeout for external calls |

---

## Load variables locally

```zsh
set -a && source .env && set +a
```

Run this before `./mvnw spring-boot:run` or `./mvnw test`.

---

## Get an API key

Register at [api.setlist.fm](https://api.setlist.fm/docs/1.0/index.html) — free account, key is issued immediately.
