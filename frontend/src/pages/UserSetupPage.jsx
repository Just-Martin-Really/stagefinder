import { useState } from 'react'
import { api } from '../api/client'

const USER_KEY = 'stagefinder_user_id'

export default function UserSetupPage() {
  const [username, setUsername] = useState('')
  const [email, setEmail] = useState('')
  const [error, setError] = useState(null)
  const [success, setSuccess] = useState(null)
  const [loading, setLoading] = useState(false)

  const existingId = localStorage.getItem(USER_KEY)

  async function handleSubmit(e) {
    e.preventDefault()
    setError(null)
    setSuccess(null)
    setLoading(true)
    try {
      const user = await api.createUser({ username, email })
      localStorage.setItem(USER_KEY, user.id)
      setSuccess(`Account created! Welcome, ${user.username}.`)
      setUsername('')
      setEmail('')
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page">
      <h1>Account Setup</h1>

      {existingId && (
        <div className="card" style={{ marginBottom: '1.5rem' }}>
          <div className="card-sub">Logged in as user ID <strong>{existingId}</strong></div>
          <div className="actions" style={{ marginTop: '0.5rem' }}>
            <button className="danger" onClick={() => { localStorage.removeItem(USER_KEY); window.location.reload() }}>
              Sign out
            </button>
          </div>
        </div>
      )}

      <h2>Create a new account</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Username</label>
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            placeholder="e.g. rockfan42"
            required
            minLength={3}
            maxLength={50}
          />
        </div>
        <div className="form-group">
          <label>Email</label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="you@example.com"
            required
          />
        </div>

        {error && <p className="error-msg">{error}</p>}
        {success && <p style={{ color: '#86efac', marginBottom: '0.8rem' }}>{success}</p>}

        <button className="primary" type="submit" disabled={loading}>
          {loading ? 'Creating…' : 'Create Account'}
        </button>
      </form>
    </div>
  )
}
