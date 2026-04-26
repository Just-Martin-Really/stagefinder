import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { api } from '../api/client'
import AddFavoriteForm from '../components/AddFavoriteForm'

export default function ArtistDetailPage({ currentUser, userLoading }) {
  const { mbid } = useParams()
  const [setlists, setSetlists] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    api.getSetlists(mbid)
      .then(setSetlists)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [mbid])

  return (
    <div className="page">
      <h1>
        Artist <span className="tag">{mbid}</span>
      </h1>

      <AddFavoriteForm mbid={mbid} currentUser={currentUser} userLoading={userLoading} />

      <h2>Setlists</h2>

      {loading && <p className="empty-msg">Loading…</p>}
      {error && <p className="error-msg">{error}</p>}

      {!loading && !error && setlists.length === 0 && (
        <p className="empty-msg">No setlists found for this artist.</p>
      )}

      {setlists.map((s) => (
        <div key={s.id} className="card">
          <div className="card-title">
            {s.eventDate}
            {s.venueName && (
              <span className="card-sub" style={{ marginLeft: '0.5rem' }}>
                @ {s.venueName}
              </span>
            )}
          </div>
          {(s.cityName || s.countryName) && (
            <div className="card-sub">
              {[s.cityName, s.countryName].filter(Boolean).join(', ')}
            </div>
          )}
          {s.songs && s.songs.length > 0 && (
            <ul className="song-list">
              {s.songs.map((song, i) => (
                <li key={i}>{song}</li>
              ))}
            </ul>
          )}
        </div>
      ))}
    </div>
  )
}
