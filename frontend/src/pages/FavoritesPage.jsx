import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api/client'

const USER_KEY = 'stagefinder_user_id'

export default function FavoritesPage() {
  const [userId] = useState(() => localStorage.getItem(USER_KEY))
  const [favorites, setFavorites] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [editingId, setEditingId] = useState(null)
  const [noteInput, setNoteInput] = useState('')
  const navigate = useNavigate()

  useEffect(() => {
    if (!userId) { setLoading(false); return }
    api.getFavorites(userId)
      .then(setFavorites)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [userId])

  async function handleRemove(favoriteId) {
    try {
      await api.removeFavorite(userId, favoriteId)
      setFavorites((prev) => prev.filter((f) => f.id !== favoriteId))
    } catch (err) {
      setError(err.message)
    }
  }

  async function handleSaveNote(favoriteId) {
    try {
      const updated = await api.updateFavoriteNote(userId, favoriteId, { note: noteInput })
      setFavorites((prev) => prev.map((f) => (f.id === favoriteId ? updated : f)))
      setEditingId(null)
    } catch (err) {
      setError(err.message)
    }
  }

  if (!userId) {
    return (
      <div className="page">
        <h1>Favorites</h1>
        <p className="empty-msg">
          No account set up yet.{' '}
          <span style={{ color: '#a78bfa', cursor: 'pointer' }} onClick={() => navigate('/setup')}>
            Create one here.
          </span>
        </p>
      </div>
    )
  }

  return (
    <div className="page">
      <h1>My Favorites</h1>
      {error && <p className="error-msg">{error}</p>}
      {loading && <p className="empty-msg">Loading…</p>}
      {!loading && favorites.length === 0 && (
        <p className="empty-msg">No favorites yet. Search for an artist and add one!</p>
      )}

      {favorites.map((fav) => (
        <div key={fav.id} className="card">
          <div
            className="card-title"
            style={{ cursor: 'pointer' }}
            onClick={() => navigate(`/artist/${fav.artist.mbid}`)}
          >
            {fav.artist.name}
          </div>
          <div className="card-sub">{fav.artist.sortName}</div>

          {editingId === fav.id ? (
            <div style={{ marginTop: '0.6rem' }}>
              <textarea
                rows={2}
                value={noteInput}
                onChange={(e) => setNoteInput(e.target.value)}
                placeholder="Add a note…"
              />
              <div className="actions">
                <button className="primary" onClick={() => handleSaveNote(fav.id)}>Save</button>
                <button className="ghost" onClick={() => setEditingId(null)}>Cancel</button>
              </div>
            </div>
          ) : (
            <>
              {fav.note && <div className="card-sub" style={{ marginTop: '0.4rem' }}>{fav.note}</div>}
              <div className="actions">
                <button className="ghost" onClick={() => { setEditingId(fav.id); setNoteInput(fav.note ?? '') }}>
                  {fav.note ? 'Edit note' : 'Add note'}
                </button>
                <button className="danger" onClick={() => handleRemove(fav.id)}>Remove</button>
              </div>
            </>
          )}
        </div>
      ))}
    </div>
  )
}
