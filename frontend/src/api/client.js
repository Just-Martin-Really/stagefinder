const BASE = '/api'

let unauthorizedHandler = null

export function setUnauthorizedHandler(fn) {
  unauthorizedHandler = fn
}

async function request(path, options = {}) {
  const { skipAuthHandler, ...fetchOpts } = options
  const res = await fetch(`${BASE}${path}`, {
    headers: { 'Content-Type': 'application/json', ...fetchOpts.headers },
    ...fetchOpts,
  })
  if (!res.ok) {
    if (res.status === 401 && !skipAuthHandler && unauthorizedHandler) {
      unauthorizedHandler()
    }
    const body = await res.json().catch(() => ({}))
    throw Object.assign(new Error(body.message ?? res.statusText), { status: res.status, body })
  }
  if (res.status === 204) return null
  return res.json()
}

export const api = {
  // Auth
  login:    (data) => request('/auth/login',  { method: 'POST', body: JSON.stringify(data) }),
  logout:   ()     => request('/auth/logout', { method: 'POST' }),
  me:       (opts) => request('/auth/me', opts),
  register: (data) => request('/users',       { method: 'POST', body: JSON.stringify(data) }),

  // Artists / setlist.fm
  searchArtists: (q, page = 1) => request(`/setlists/search?q=${encodeURIComponent(q)}&page=${page}`),
  getSetlists: (mbid, page = 1) => request(`/setlists/${mbid}?page=${page}`),
  getArtistStats: (mbid) => request(`/artists/mbid/${mbid}/stats`),

  // Feed
  getFeed: (userId) => request(`/users/${userId}/feed`),

  // Favorites
  getFavorites: (userId) => request(`/users/${userId}/favorites`),
  addFavorite: (userId, data) =>
    request(`/users/${userId}/favorites`, { method: 'POST', body: JSON.stringify(data) }),
  updateFavoriteNote: (userId, favoriteId, data) =>
    request(`/users/${userId}/favorites/${favoriteId}`, { method: 'PATCH', body: JSON.stringify(data) }),
  removeFavorite: (userId, favoriteId) =>
    request(`/users/${userId}/favorites/${favoriteId}`, { method: 'DELETE' }),
}
