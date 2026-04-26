import { useState, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api/client'
import Hero from '../components/Hero'

export default function ArtistSearchPage() {
  const [query, setQuery] = useState('')
  const [results, setResults] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const navigate = useNavigate()
  const resultsRef = useRef(null)

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
      setTimeout(() => resultsRef.current?.scrollIntoView({ behavior: 'smooth', block: 'start' }), 50)
      setLoading(false)
    }
  }

  return (
    <>
      <Hero query={query} setQuery={setQuery} onSearch={handleSearch} loading={loading} />
      <div className="page" ref={resultsRef}>
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
    </>
  )
}
