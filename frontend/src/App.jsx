import { useState, useEffect } from 'react'
import { BrowserRouter, Routes, Route, NavLink } from 'react-router-dom'
import { api } from './api/client'
import ArtistSearchPage from './pages/ArtistSearchPage'
import ArtistDetailPage from './pages/ArtistDetailPage'
import FavoritesPage from './pages/FavoritesPage'
import UserSetupPage from './pages/UserSetupPage'
import AuthModal from './components/AuthModal'

export default function App() {
  const [currentUser, setCurrentUser] = useState(null)
  const [userLoading, setUserLoading] = useState(true)
  const [modal, setModal] = useState(null)

  useEffect(() => {
    api.me().then(setCurrentUser).catch(() => {}).finally(() => setUserLoading(false))
  }, [])

  async function handleLogout() {
    try { await api.logout() } catch {}
    setCurrentUser(null)
  }

  return (
    <BrowserRouter>
      <nav>
        <NavLink to="/" className="brand" end>Stagefinder</NavLink>
        <NavLink to="/favorites">Favorites</NavLink>
        <div className="nav-right">
          <a href="https://just-martin-really.github.io/stagefinder/" target="_blank" rel="noopener noreferrer" className="nav-docs">Docs</a>
          {currentUser ? (
            <>
              <span className="nav-user">{currentUser.username}</span>
              <button className="nav-btn ghost" onClick={handleLogout}>Logout</button>
            </>
          ) : (
            <>
              <button className="nav-btn ghost" onClick={() => setModal('login')}>Login</button>
              <button className="nav-btn primary" onClick={() => setModal('register')}>Register</button>
            </>
          )}
        </div>
      </nav>
      <Routes>
        <Route path="/" element={<ArtistSearchPage currentUser={currentUser} userLoading={userLoading} />} />
        <Route path="/artist/:mbid" element={<ArtistDetailPage currentUser={currentUser} userLoading={userLoading} />} />
        <Route path="/favorites" element={<FavoritesPage currentUser={currentUser} userLoading={userLoading} />} />
        <Route path="/setup" element={<UserSetupPage />} />
      </Routes>
      {modal && (
        <AuthModal
          defaultTab={modal}
          onSuccess={(user) => { setCurrentUser(user); setModal(null) }}
          onClose={() => setModal(null)}
        />
      )}
    </BrowserRouter>
  )
}
