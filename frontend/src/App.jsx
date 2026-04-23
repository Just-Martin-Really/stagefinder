import { BrowserRouter, Routes, Route, NavLink } from 'react-router-dom'
import ArtistSearchPage from './pages/ArtistSearchPage'
import ArtistDetailPage from './pages/ArtistDetailPage'
import FavoritesPage from './pages/FavoritesPage'
import UserSetupPage from './pages/UserSetupPage'

export default function App() {
  return (
    <BrowserRouter>
      <nav>
        <span className="brand">Stagefinder</span>
        <NavLink to="/">Search</NavLink>
        <NavLink to="/favorites">Favorites</NavLink>
        <NavLink to="/setup">Account</NavLink>
      </nav>
      <Routes>
        <Route path="/" element={<ArtistSearchPage />} />
        <Route path="/artist/:mbid" element={<ArtistDetailPage />} />
        <Route path="/favorites" element={<FavoritesPage />} />
        <Route path="/setup" element={<UserSetupPage />} />
      </Routes>
    </BrowserRouter>
  )
}
