import { createContext, useContext, useEffect, useState } from 'react'
import { api, setUnauthorizedHandler } from '../api/client'
import AuthModal from '../components/AuthModal'

const AuthContext = createContext(null)

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used inside <AuthProvider>')
  return ctx
}

export function AuthProvider({ children }) {
  const [currentUser, setCurrentUser] = useState(null)
  const [userLoading, setUserLoading] = useState(true)
  const [modal, setModal] = useState(null)

  useEffect(() => {
    api.me({ skipAuthHandler: true })
      .then(setCurrentUser)
      .catch(() => {})
      .finally(() => setUserLoading(false))
  }, [])

  useEffect(() => {
    setUnauthorizedHandler(() => {
      setCurrentUser(null)
      setModal('login')
    })
    return () => setUnauthorizedHandler(null)
  }, [])

  function clearAuth() {
    setCurrentUser(null)
  }

  function requestAuth(mode = 'login') {
    setModal(mode)
  }

  const value = { currentUser, userLoading, setCurrentUser, clearAuth, requestAuth }

  return (
    <AuthContext.Provider value={value}>
      {children}
      {modal && (
        <AuthModal
          defaultTab={modal}
          onSuccess={(user) => { setCurrentUser(user); setModal(null) }}
          onClose={() => setModal(null)}
        />
      )}
    </AuthContext.Provider>
  )
}
