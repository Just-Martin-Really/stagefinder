# Frontend

> React 19 + Vite SPA — four pages, one API client, no external dependencies beyond React Router.

---

## Routes

| Path | Component | Description |
|------|-----------|-------------|
| `/` | `ArtistSearchPage` | Feed (logged in) or search results |
| `/artist/:mbid` | `ArtistDetailPage` | Stats and setlist history for one artist |
| `/favorites` | `FavoritesPage` | User's saved favorites |
| `/setup` | `UserSetupPage` | Create or switch user account |

---

## Navigation

The top nav shows **Favorites** and auth controls. The **Stagefinder** brand is a link to `/`. There is no separate Search tab — search is embedded in the hero on the landing page.

---

## Pages

### Landing (`/`)

The hero contains a search form. Below it:

- **Logged in, no search performed** — calls `GET /api/users/{userId}/feed` and renders the merged setlist feed sorted by date. Each card shows artist name (links to `/artist/:mbid`), date, venue, city, song count, and a setlist.fm link.
- **Search performed** — replaces the feed with artist search results from `GET /api/setlists/search?q=`. Clicking a result navigates to `/artist/:mbid`.
- **Not logged in** — feed is not shown; only search results appear after a query.

### Artist detail (`/artist/:mbid`)

Calls `GET /api/artists/mbid/{mbid}/stats` and `GET /api/setlists/{mbid}` in parallel. The artist name from the stats response is used as the page heading.

**Stats grid** (rendered once stats load):

| Stat | Source |
|------|--------|
| Shows analysed · date range | `totalShows`, `oldestShowDate`, `newestShowDate` |
| Total song plays | `totalSongPlays` |
| Most played songs (top 10) | `topSongs[].name` + `topSongs[].count` |
| Most played venues (top 5) | `topVenues[].name`, `.city`, `.count` |

Below the stats grid, the full setlist list from `GET /api/setlists/{mbid}` shows date, venue, city, and song list per show.

### Favorites (`/favorites`)

Lists the current user's favorites from `GET /api/users/{userId}/favorites`. Each card shows the artist name, note, and a remove button. Requires login — shows a prompt otherwise.

### Account (`/setup`)

Creates a user via `POST /api/users` or loads an existing one.

---

## API client

`frontend/src/api/client.js` — fetch wrapper around the backend. All pages import from here; no page calls `fetch` directly.

| Method | Path |
|--------|------|
| `api.searchArtists(q, page)` | `GET /api/setlists/search` |
| `api.getSetlists(mbid, page)` | `GET /api/setlists/{mbid}` |
| `api.getArtistStats(mbid)` | `GET /api/artists/mbid/{mbid}/stats` |
| `api.getFeed(userId)` | `GET /api/users/{userId}/feed` |
| `api.getFavorites(userId)` | `GET /api/users/{userId}/favorites` |
| `api.addFavorite(userId, data)` | `POST /api/users/{userId}/favorites` |
| `api.updateFavoriteNote(userId, favId, data)` | `PATCH /api/users/{userId}/favorites/{favId}` |
| `api.removeFavorite(userId, favId)` | `DELETE /api/users/{userId}/favorites/{favId}` |
| `api.login(data)` | `POST /api/auth/login` |
| `api.logout()` | `POST /api/auth/logout` |
| `api.me()` | `GET /api/auth/me` |
| `api.register(data)` | `POST /api/users` |

---

## Dev server

```zsh
cd frontend
npm run dev
# → http://localhost:5173
```

Vite proxies `/api/*` to `http://localhost:8080` in dev mode, so no CORS config is needed during development.

---

## Production build

```zsh
cd frontend
npm run build
```

Output goes to `frontend/dist/`. The Docker build copies this into the Spring Boot jar so the backend serves the frontend as static files from `/`.
