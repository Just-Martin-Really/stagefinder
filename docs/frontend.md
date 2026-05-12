# Frontend

> React 19 + Vite SPA — three pages, one API client, no external dependencies beyond React Router.

---

## Routes

| Path | Component | Description |
|------|-----------|-------------|
| `/` | `ArtistSearchPage` | Feed (logged in) or search results |
| `/artist/:mbid` | `ArtistDetailPage` | Stats and setlist history for one artist |
| `/favorites` | `FavoritesPage` | User's saved favorites |

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

Lists the current user's favorites from `GET /api/users/{userId}/favorites`. Each card shows the artist name, note, and a remove button. Requires login, otherwise shows a prompt.

---

## Auth state

`frontend/src/auth/AuthContext.jsx` provides `AuthProvider` and the `useAuth()` hook. `App.jsx` wraps the routes in `AuthProvider`; pages and components read `currentUser` and `userLoading` from `useAuth()` instead of receiving props.

`useAuth()` returns:

| Field | Description |
|-------|-------------|
| `currentUser` | The logged-in user, or `null` |
| `userLoading` | `true` while the initial `GET /api/auth/me` probe is in flight |
| `setCurrentUser(user)` | Set after a successful login |
| `clearAuth()` | Clear local auth state, e.g. on logout |
| `requestAuth(mode)` | Open the auth modal in `'login'` or `'register'` mode |

The provider registers a global 401 handler with the API client. Any API call that returns 401 (other than the boot `me()` probe) clears `currentUser` and opens the login modal. After a successful login the modal closes; the user retries the failed action manually.

## API client

`frontend/src/api/client.js` — fetch wrapper around the backend. All pages import from here; no page calls `fetch` directly.

`setUnauthorizedHandler(fn)` registers a callback invoked when any request returns 401. Pass `{ skipAuthHandler: true }` in the options to opt out for a specific call (used internally by the auth boot probe).

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
