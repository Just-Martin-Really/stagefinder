import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { api } from '../api/client'
import AddFavoriteForm from '../components/AddFavoriteForm'

export default function ArtistDetailPage({ currentUser, userLoading }) {
  const { mbid } = useParams()

  const [stats, setStats] = useState(null)
  const [statsError, setStatsError] = useState(null)

  const [setlists, setSetlists] = useState([])
  const [setlistsLoading, setSetlistsLoading] = useState(true)
  const [setlistsError, setSetlistsError] = useState(null)

  useEffect(() => {
    api.getArtistStats(mbid).then(setStats).catch((err) => setStatsError(err.message))
    api.getSetlists(mbid)
      .then(setSetlists)
      .catch((err) => setSetlistsError(err.message))
      .finally(() => setSetlistsLoading(false))
  }, [mbid])

  const artistName = stats?.name ?? mbid

  return (
    <div className="page">
      <h1>{artistName}</h1>

      <AddFavoriteForm mbid={mbid} currentUser={currentUser} userLoading={userLoading} />

      {statsError && <p className="error-msg">{statsError}</p>}

      {stats && (
        <div className="stats-grid">
          <div className="stat-card">
            <div className="stat-value">{stats.totalShows}</div>
            <div className="stat-label">
              Shows analysed
              {stats.oldestShowDate && stats.newestShowDate && (
                <> · {fmtMonthYear(stats.oldestShowDate)} – {fmtMonthYear(stats.newestShowDate)}</>
              )}
            </div>
          </div>
          <div className="stat-card">
            <div className="stat-value">{stats.totalSongPlays}</div>
            <div className="stat-label">Total song plays</div>
          </div>
          {stats.topSongs.length > 0 && (
            <div className="stat-card stat-card--wide">
              <div className="stat-label stat-label--heading">Most played songs</div>
              <ol className="stat-list">
                {stats.topSongs.map((s) => (
                  <li key={s.name}>
                    <span className="stat-list-name">{s.name}</span>
                    <span className="stat-list-count">{s.count}×</span>
                  </li>
                ))}
              </ol>
            </div>
          )}
          {stats.topVenues.length > 0 && (
            <div className="stat-card stat-card--wide">
              <div className="stat-label stat-label--heading">Most played venues</div>
              <ol className="stat-list">
                {stats.topVenues.map((v) => (
                  <li key={`${v.name}-${v.city}`}>
                    <span className="stat-list-name">
                      {v.name}{v.city ? `, ${v.city}` : ''}
                    </span>
                    <span className="stat-list-count">{v.count}×</span>
                  </li>
                ))}
              </ol>
            </div>
          )}
        </div>
      )}

      <h2>Setlists</h2>

      {setlistsLoading && <p className="empty-msg">Loading…</p>}
      {setlistsError && <p className="error-msg">{setlistsError}</p>}

      {!setlistsLoading && !setlistsError && setlists.length === 0 && (
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

function fmtMonthYear(ddmmyyyy) {
  if (!ddmmyyyy) return ''
  const [dd, mm, yyyy] = ddmmyyyy.split('-')
  return new Date(`${yyyy}-${mm}-${dd}`).toLocaleDateString('en-GB', { month: 'short', year: 'numeric' })
}
