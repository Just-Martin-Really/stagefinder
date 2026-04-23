# Stagefinder

Stagefinder is a Spring Boot + React project for discovering artist events and managing user favorites.

## Current technical baseline

- Backend: Java 21, Spring Boot, Maven Wrapper
- Persistence: Spring Data JPA + H2 (development)
- Validation: Bean Validation (`@Valid`, constraints)
- External integration target: setlist.fm (through backend only)

## API key setup (setlist.fm)

Do not commit real keys. Keep secrets in a local `.env` file (ignored by git).

```zsh
cd /Users/martinliess/dhbw/WebEng2/stagefinder
cp .env.example .env
```

Then edit `.env` and set:

```dotenv
SETLISTFM_API_KEY=your_real_key_here
```

Optional overrides:

- `SETLISTFM_BASE_URL` (default: `https://api.setlist.fm`)
- `SETLISTFM_API_VERSION` (default: `1.0`)
- `SETLISTFM_TIMEOUT_SECONDS` (default: `10`)

## Local run

```zsh
cd /Users/martinliess/dhbw/WebEng2/stagefinder
set -a
source .env
set +a
./mvnw spring-boot:run
```

## Tests

```zsh
cd /Users/martinliess/dhbw/WebEng2/stagefinder
set -a
source .env
set +a
./mvnw test
```

## Live setlist.fm connectivity test (optional)

This project includes an opt-in live test that checks whether your API key can reach setlist.fm.
It is skipped by default so regular test runs stay deterministic.

```zsh
cd /Users/martinliess/dhbw/WebEng2/stagefinder
set -a
source .env
set +a
RUN_SETLISTFM_LIVE_TESTS=true ./mvnw -Dtest=SetlistFmLiveIntegrationTest test
```

