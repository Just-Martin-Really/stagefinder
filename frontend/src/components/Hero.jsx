const PARTICLES = [
  { top: '12%', left: '7%',  size: 3, delay: 0    },
  { top: '22%', left: '91%', size: 2, delay: 1.5  },
  { top: '62%', left: '4%',  size: 4, delay: 0.8  },
  { top: '78%', left: '87%', size: 2, delay: 2.2  },
  { top: '38%', left: '94%', size: 3, delay: 0.4  },
  { top: '83%', left: '14%', size: 2, delay: 1.8  },
  { top: '9%',  left: '44%', size: 2, delay: 1.1  },
  { top: '88%', left: '58%', size: 3, delay: 0.6  },
  { top: '52%', left: '2%',  size: 2, delay: 2.5  },
  { top: '32%', left: '48%', size: 1.5, delay: 1.3 },
  { top: '68%', left: '72%', size: 2, delay: 0.9  },
  { top: '18%', left: '28%', size: 1.5, delay: 2.0 },
]

export default function Hero({ query, setQuery, onSearch, loading }) {
  return (
    <section className="hero" aria-label="Stagefinder">
      <div className="hero-bg" aria-hidden="true">
        <div className="hero-orb orb-1" />
        <div className="hero-orb orb-2" />
        <div className="hero-orb orb-3" />
        <div className="hero-orb orb-4" />
        {PARTICLES.map((p, i) => (
          <span
            key={i}
            className="hero-particle"
            style={{
              top: p.top,
              left: p.left,
              width: `${p.size}px`,
              height: `${p.size}px`,
              animationDelay: `${p.delay}s`,
            }}
          />
        ))}
      </div>

      <div className="hero-content">
        <h1 className="hero-title" aria-label="Stagefinder">
          <span className="hero-title-line">STAGE</span>
          <span className="hero-title-line hero-title-line-2">FINDER</span>
        </h1>
        <p className="hero-tagline">Find your sound.&nbsp;&nbsp;Own the night.</p>
        <form className="hero-search-form" onSubmit={onSearch}>
          <input
            className="hero-search-input"
            type="text"
            placeholder="Search artists…"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            aria-label="Search artists"
            autoComplete="off"
          />
          <button className="hero-search-btn" type="submit" disabled={loading}>
            {loading ? '…' : 'Search'}
          </button>
        </form>
      </div>

      <div className="hero-scroll" aria-hidden="true">
        <span className="hero-scroll-arrow">↓</span>
      </div>
    </section>
  )
}
