import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api/client'

export default function ArtistSearchPage() {
  const [query, setQuery] = useState('')
  const [results, setResults] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const navigate = useNavigate()

  async function handleSearch(e) {
    e.preventDefault()
    if (!query.trim()) return
    setLoading(true)
    setError(null)
    try {
      const data = await api.searchArtists(query.trim())
      setResults(data)
      if (data.length === 0) setError('No artists found.')
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page">
      <h1>Find Artists</h1>
      <form className="search-row" onSubmit={handleSearch}>
        <input
          type="text"
          placeholder="Search artists…"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
        />
        <button className="primary" type="submit" disabled={loading}>
          {loading ? 'Searching…' : 'Search'}
        </button>
      </form>

      {error && <p className="error-msg">{error}</p>}

      {results.map((artist) => (
        <div
          key={artist.mbid}
          className="card"
          style={{ cursor: 'pointer' }}
          onClick={() => navigate(`/artist/${artist.mbid}`)}
        >
          <div className="card-title">{artist.name}</div>
          {artist.sortName && artist.sortName !== artist.name && (
            <div className="card-sub">{artist.sortName}</div>
          )}
        </div>
      ))}
    </div>
  )
}
