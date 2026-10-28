import { NavLink } from 'react-router-dom'
import { useState, useEffect } from 'react'
import { notificationApi } from '../api'

export default function Navbar() {
  const [count, setCount] = useState(0)

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
    </nav>
  )
}
