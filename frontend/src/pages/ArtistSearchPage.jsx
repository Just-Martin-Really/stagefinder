import { useState, useRef, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api/client'
import Hero from '../components/Hero'

export default function ArtistSearchPage({ currentUser, userLoading }) {
  const [query, setQuery] = useState('')
  const [results, setResults] = useState([])
  const [searchLoading, setSearchLoading] = useState(false)
  const [searchError, setSearchError] = useState(null)
  const [searched, setSearched] = useState(false)

  const [feed, setFeed] = useState([])
  const [feedLoading, setFeedLoading] = useState(false)
  const [feedError, setFeedError] = useState(null)

  const navigate = useNavigate()
  const resultsRef = useRef(null)

  useEffect(() => {
    if (!currentUser) return
    setFeedLoading(true)
    api.getFeed(currentUser.id)
      .then(setFeed)
      .catch((err) => setFeedError(err.message))
      .finally(() => setFeedLoading(false))
  }, [currentUser])

  async function handleSearch(e) {
    e.preventDefault()
    if (!query.trim()) return
    setSearchLoading(true)
    setSearchError(null)
    setSearched(true)
    try {
      const data = await api.searchArtists(query.trim())
      setResults(data)
      if (data.length === 0) setSearchError('No artists found.')
    } catch (err) {
      setSearchError(err.message)
    } finally {
      setTimeout(() => resultsRef.current?.scrollIntoView({ behavior: 'smooth', block: 'start' }), 50)
      setSearchLoading(false)
    }
  }

  const showFeed = !userLoading && currentUser && !searched

  return (
    <>
      <Hero query={query} setQuery={setQuery} onSearch={handleSearch} loading={searchLoading} />
      <div className="page" ref={resultsRef}>
        {searched ? (
          <>
            {searchError && <p className="error-msg">{searchError}</p>}
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
          </>
        ) : showFeed ? (
          <>
            <h2 className="feed-heading">Recent Shows</h2>
            {feedError && <p className="error-msg">{feedError}</p>}
            {feedLoading && <p className="empty-msg">Loading feed…</p>}
            {!feedLoading && feed.length === 0 && !feedError && (
              <p className="empty-msg">Add some favorites to see their recent shows here.</p>
            )}
            {feed.map((item, i) => (
              <div key={`${item.artistMbid}-${item.eventDate}-${i}`} className="card feed-card">
                <div
                  className="card-title"
                  style={{ cursor: 'pointer' }}
                  onClick={() => navigate(`/artist/${item.artistMbid}`)}
                >
                  {item.artistName}
                </div>
                <div className="card-sub feed-date">{formatDate(item.eventDate)}</div>
                {(item.venueName || item.cityName) && (
                  <div className="card-sub">
                    {[item.venueName, item.cityName, item.countryName].filter(Boolean).join(', ')}
                  </div>
                )}
                {item.songCount > 0 && (
                  <div className="card-sub">{item.songCount} songs</div>
                )}
                {item.url && (
                  <div className="actions">
                    <a className="ghost btn-link" href={item.url} target="_blank" rel="noopener noreferrer">
                      View setlist
                    </a>
                  </div>
                )}
              </div>
            ))}
          </>
        ) : null}
      </div>
    </>
  )
}

function formatDate(ddmmyyyy) {
  if (!ddmmyyyy) return ''
  const [dd, mm, yyyy] = ddmmyyyy.split('-')
  return new Date(`${yyyy}-${mm}-${dd}`).toLocaleDateString('en-GB', {
    day: 'numeric', month: 'short', year: 'numeric',
  })
}
