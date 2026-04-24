# REST API

> All endpoints are served at `http://localhost:8080`. Interactive docs at `/swagger-ui.html`.

---

## Users

| Method | Path | Status | Description |
|--------|------|--------|-------------|
| `GET` | `/api/users` | 200 | List all users |
| `GET` | `/api/users/{id}` | 200 / 404 | Get user by ID |
| `POST` | `/api/users` | 201 | Create a user |
| `PUT` | `/api/users/{id}` | 200 / 404 | Replace user |
| `DELETE` | `/api/users/{id}` | 204 / 404 | Delete user |

### Request body — create / update user

```json
{
  "username": "martin",
  "email": "martin@example.com"
}
```

| Field | Type | Constraints |
|-------|------|-------------|
| `username` | string | 3–50 characters, required |
| `email` | string | valid email format, required |

### Response — user

```json
{
  "id": 1,
  "username": "martin",
  "email": "martin@example.com"
}
```

---

## Favorites

| Method | Path | Status | Description |
|--------|------|--------|-------------|
| `GET` | `/api/users/{userId}/favorites` | 200 | List user's favorites |
| `POST` | `/api/users/{userId}/favorites` | 201 | Add a favorite |
| `PATCH` | `/api/users/{userId}/favorites/{favoriteId}` | 200 | Update note |
| `DELETE` | `/api/users/{userId}/favorites/{favoriteId}` | 204 | Remove favorite |

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

Adding a favorite fetches the artist from setlist.fm if it isn't cached yet.

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

| Method | Path | Status | Description |
|--------|------|--------|-------------|
| `GET` | `/api/setlists/search?q={query}` | 200 | Search artists by name |
| `GET` | `/api/setlists/search?q={query}&page={n}` | 200 | Paginated artist search |
| `GET` | `/api/setlists/{mbid}` | 200 | Get setlists for an artist |
| `GET` | `/api/setlists/{mbid}?page={n}` | 200 | Paginated setlists |

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

| Method | Path | Status | Description |
|--------|------|--------|-------------|
| `GET` | `/api/artists` | 200 | List all cached artists |
| `GET` | `/api/artists/{id}` | 200 / 404 | Get cached artist by local ID |

Artists are only added to the local cache when favorited.
