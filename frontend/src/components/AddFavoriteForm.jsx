import { useState } from 'react'
import { api } from '../api/client'

export default function AddFavoriteForm({ mbid, currentUser }) {
  const [note, setNote] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [success, setSuccess] = useState(false)

  if (!currentUser) return (
    <div className="card" style={{ marginBottom: '1rem' }}>
      <span className="card-sub">Log in to save this artist to your favorites.</span>
    </div>
  )

  async function handleSubmit(e) {
    e.preventDefault()
    setError(null)
    setSuccess(false)
    setLoading(true)
    try {
      await api.addFavorite(currentUser.id, { mbid, note })
      setSuccess(true)
      setNote('')
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="card" style={{ marginBottom: '1.5rem' }}>
      <h2 style={{ marginBottom: '0.6rem' }}>Add to Favorites</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Note (optional)</label>
          <input
            type="text"
            value={note}
            onChange={(e) => setNote(e.target.value)}
            placeholder="e.g. Saw them live in 2023"
            maxLength={500}
          />
        </div>
        {error && <p className="error-msg">{error}</p>}
        {success && <p style={{ color: '#86efac', marginBottom: '0.5rem' }}>Added to favorites!</p>}
        <button className="primary" type="submit" disabled={loading}>
          {loading ? 'Saving…' : 'Add to Favorites'}
        </button>
      </form>
    </div>
  )
}
