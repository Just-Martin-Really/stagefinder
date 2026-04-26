import { useState } from 'react'
import { api } from '../api/client'

export default function AuthModal({ defaultTab = 'login', onSuccess, onClose }) {
  const [tab, setTab] = useState(defaultTab)
  const [username, setUsername] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  function switchTab(t) {
    setTab(t)
    setUsername('')
    setEmail('')
    setPassword('')
    setError(null)
  }

  async function handleLogin(e) {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      const user = await api.login({ username, password })
      onSuccess(user)
    } catch (err) {
      setError(err.message || 'Login failed.')
    } finally {
      setLoading(false)
    }
  }

  async function handleRegister(e) {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      await api.register({ username, email, password })
      const user = await api.login({ username, password })
      onSuccess(user)
    } catch (err) {
      setError(err.message || 'Registration failed.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-tabs">
          <button className={`modal-tab${tab === 'login' ? ' active' : ''}`} onClick={() => switchTab('login')}>Login</button>
          <button className={`modal-tab${tab === 'register' ? ' active' : ''}`} onClick={() => switchTab('register')}>Register</button>
          <button className="modal-close" onClick={onClose} aria-label="Close">✕</button>
        </div>

        {tab === 'login' ? (
          <form onSubmit={handleLogin}>
            <div className="form-group">
              <label>Username</label>
              <input type="text" value={username} onChange={(e) => setUsername(e.target.value)} required autoFocus />
            </div>
            <div className="form-group">
              <label>Password</label>
              <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
            </div>
            {error && <p className="error-msg">{error}</p>}
            <button className="primary" type="submit" disabled={loading} style={{ width: '100%' }}>
              {loading ? 'Signing in…' : 'Sign in'}
            </button>
          </form>
        ) : (
          <form onSubmit={handleRegister}>
            <div className="form-group">
              <label>Username</label>
              <input type="text" value={username} onChange={(e) => setUsername(e.target.value)} required minLength={3} maxLength={50} autoFocus />
            </div>
            <div className="form-group">
              <label>Email</label>
              <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
            </div>
            <div className="form-group">
              <label>Password</label>
              <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required minLength={8} />
            </div>
            {error && <p className="error-msg">{error}</p>}
            <button className="primary" type="submit" disabled={loading} style={{ width: '100%' }}>
              {loading ? 'Creating account…' : 'Create account'}
            </button>
          </form>
        )}
      </div>
    </div>
  )
}
