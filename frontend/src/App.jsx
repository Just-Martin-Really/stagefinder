import { BrowserRouter, Routes, Route, NavLink } from 'react-router-dom'
import { api } from './api/client'
import { AuthProvider, useAuth } from './auth/AuthContext'
import ArtistSearchPage from './pages/ArtistSearchPage'
import ArtistDetailPage from './pages/ArtistDetailPage'
import FavoritesPage from './pages/FavoritesPage'

function NavAuth() {
  const { currentUser, clearAuth, requestAuth } = useAuth()

  async function handleLogout() {
    try { await api.logout() } catch {}
    clearAuth()
  }

  if (currentUser) {
    return (
      <>
        <span className="nav-user">{currentUser.username}</span>
        <button className="nav-btn ghost" onClick={handleLogout}>Logout</button>
      </>
    )
  }
  return (
    <>
      <button className="nav-btn ghost" onClick={() => requestAuth('login')}>Login</button>
      <button className="nav-btn primary" onClick={() => requestAuth('register')}>Register</button>
    </>
  )
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <nav>
          <NavLink to="/" className="brand" end>Stagefinder</NavLink>
          <NavLink to="/favorites">Favorites</NavLink>
          <div className="nav-right">
            <a href="https://just-martin-really.github.io/stagefinder/" target="_blank" rel="noopener noreferrer" className="nav-docs">Docs</a>
            <NavAuth />
          </div>
        </nav>
        <Routes>
          <Route path="/" element={<ArtistSearchPage />} />
          <Route path="/artist/:mbid" element={<ArtistDetailPage />} />
          <Route path="/favorites" element={<FavoritesPage />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}
