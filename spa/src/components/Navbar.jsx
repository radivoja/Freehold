import { NavLink } from 'react-router-dom'
import { useState, useEffect } from 'react'
import { notificationApi } from '../api'
import { useAuth } from '../auth/AuthContext'

export default function Navbar() {
  const [count, setCount] = useState(0)
  const { user, logout } = useAuth()

  useEffect(() => {
    const poll = () => notificationApi.getCount().then(d => setCount(d.count)).catch(() => {})
    poll()
    const id = setInterval(poll, 10000)
    return () => clearInterval(id)
  }, [])

  return (
    <nav className="navbar">
      <NavLink to="/" className="logo">Freehold</NavLink>
      <div className="nav-links">
        <NavLink to="/goods">Goods</NavLink>
        <NavLink to="/marketplace">Marketplace</NavLink>
        <NavLink to="/orders">Orders</NavLink>
        <NavLink to="/notifications" className="notif-link">
          Notifications
          {count > 0 && <span className="badge">{count}</span>}
        </NavLink>
      </div>
      {user && (
        <div className="nav-user">
          <span className="user-email">{user.email}</span>
          <button className="sign-out-btn" onClick={logout}>Sign Out</button>
        </div>
      )}
    </nav>
  )
}
