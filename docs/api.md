# REST API

> All endpoints are served at `http://localhost:8080`. Interactive docs at `/swagger-ui.html`.

Most endpoints require authentication — send requests with a valid session cookie obtained via `POST /api/auth/login`.

---

## Authentication

| Method | Path | Auth | Status | Description |
|--------|------|------|--------|-------------|
| `POST` | `/api/auth/login` | — | 200 / 401 | Log in, receive session cookie |
| `GET` | `/api/auth/me` | required | 200 / 401 | Get current user |
| `POST` | `/api/auth/logout` | required | 204 | Invalidate session |

### Request body — login

```json
{
  "username": "martin",
  "password": "securepass"
}
```

### Response — login / me

```json
{
  "id": 1,
  "username": "martin",
  "email": "martin@example.com",
  "createdAt": "2025-03-01T10:00:00"
}
```

On success, the server sets a `JSESSIONID` cookie. Include it in subsequent requests. The session lasts 24 hours.

---

## Users

| Method | Path | Auth | Status | Description |
|--------|------|------|--------|-------------|
| `POST` | `/api/users` | — | 201 | Register a new user |
| `GET` | `/api/users/{id}` | required | 200 / 401 / 404 | Get user by ID |
| `PUT` | `/api/users/{id}` | required (owner) | 200 / 401 / 403 / 404 | Update user |
| `DELETE` | `/api/users/{id}` | required (owner) | 204 / 401 / 403 / 404 | Delete user |

### Request body — register / update user

```json
{
  "username": "martin",
  "email": "martin@example.com",
  "password": "securepass"
}
```

| Field | Type | Constraints |
|-------|------|-------------|
| `username` | string | 3–50 characters, required |
| `email` | string | valid email format, required |
| `password` | string | 8–100 characters, required |

### Response — user

```json
{
  "id": 1,
  "username": "martin",
  "email": "martin@example.com",
  "createdAt": "2025-03-01T10:00:00"
}
```

!!! warning "Ownership"
    `PUT` and `DELETE` on a user resource require the authenticated session to belong to that user. A different user receives `403 Forbidden`.

---

## Favorites

| Method | Path | Auth | Status | Description |
|--------|------|------|--------|-------------|
| `GET` | `/api/users/{userId}/favorites` | required (owner) | 200 / 401 / 403 | List user's favorites |
| `POST` | `/api/users/{userId}/favorites` | required (owner) | 201 / 401 / 403 / 409 | Add a favorite |
| `PATCH` | `/api/users/{userId}/favorites/{favoriteId}` | required (owner) | 200 / 401 / 403 | Update note |
| `DELETE` | `/api/users/{userId}/favorites/{favoriteId}` | required (owner) | 204 / 401 / 403 | Remove favorite |

### Request body — add favorite

```json
{
  "mbid": "b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d",
  "note": "Saw them at Rock am Ring 2019"
}
```

| Field | Type | Constraints |
|-------|------|-------------|
| `mbid` | string | MusicBrainz ID, required |
| `note` | string | max 500 characters, optional |

Adding a favorite fetches the artist from setlist.fm if it isn't cached locally yet.

### Request body — update note

```json
{
  "note": "Updated note"
}
```

### Response — favorite

```json
{
  "id": 3,
  "userId": 1,
  "artist": {
    "id": 7,
    "mbid": "b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d",
    "name": "Radiohead",
    "sortName": "Radiohead",
    "url": "https://www.setlist.fm/setlists/radiohead-3d6bfe00.html"
  },
  "note": "Saw them at Rock am Ring 2019",
  "createdAt": "2024-11-15T14:22:00"
}
```

---

## Setlists (setlist.fm proxy)

| Method | Path | Auth | Status | Description |
|--------|------|------|--------|-------------|
| `GET` | `/api/setlists/search?q={query}` | — | 200 | Search artists by name |
| `GET` | `/api/setlists/search?q={query}&page={n}` | — | 200 | Paginated artist search |
| `GET` | `/api/setlists/{mbid}` | — | 200 | Get setlists for an artist |
| `GET` | `/api/setlists/{mbid}?page={n}` | — | 200 | Paginated setlists |

Setlist endpoints are public — no session required.

### Response — artist search

```json
[
  {
    "id": null,
    "mbid": "b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d",
    "name": "Radiohead",
    "sortName": "Radiohead",
    "url": "https://www.setlist.fm/setlists/radiohead-3d6bfe00.html"
  }
]
```

### Response — setlist

```json
[
  {
    "id": "63de4a49",
    "eventDate": "01-06-2012",
    "venueName": "Rock Werchter",
    "cityName": "Werchter",
    "countryName": "Belgium",
    "songs": ["Bloom", "Morning Mr Magpie", "Little by Little"],
    "url": "https://www.setlist.fm/setlist/radiohead/..."
  }
]
```

---

## Artists (local cache)

| Method | Path | Auth | Status | Description |
|--------|------|------|--------|-------------|
| `GET` | `/api/artists` | required | 200 | List all cached artists |
| `GET` | `/api/artists/{id}` | required | 200 / 404 | Get cached artist by local ID |

Artists are added to the local cache when first favorited.
