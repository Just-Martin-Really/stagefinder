# Frontend

> React 19 + Vite SPA — four pages, one API client, no external dependencies beyond React Router.

---

## Routes

| Path | Component | Description |
|------|-----------|-------------|
| `/` | `ArtistSearchPage` | Search artists by name |
| `/artist/:mbid` | `ArtistDetailPage` | Setlist history for one artist |
| `/favorites` | `FavoritesPage` | User's saved favorites |
| `/setup` | `UserSetupPage` | Create or switch user account |

---

## Pages

### Search (`/`)

Calls `GET /api/setlists/search?q=` on submit. Results are artist cards — clicking one navigates to `/artist/:mbid`.

### Artist detail (`/artist/:mbid`)

Calls `GET /api/setlists/{mbid}` to load setlists. Shows date, venue, city, and song list per setlist. Includes an "Add to favorites" action that posts to `/api/users/{userId}/favorites`.

### Favorites (`/favorites`)

Lists the current user's favorites from `GET /api/users/{userId}/favorites`. Each card shows the artist name, note, and a remove button.

### Account (`/setup`)

Creates a user via `POST /api/users` or loads an existing one. Stores the user ID in `localStorage` — all other pages read from there.

---

## API client

`frontend/src/api/client.js` — typed fetch wrapper around the backend. All pages import from here; no page calls `fetch` directly.

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
