import { useState, useEffect } from 'react'
import { notificationApi } from '../api'

export default function Notifications() {
  const [notifications, setNotifications] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const load = () => notificationApi.getAll()
      .then(data => { setNotifications(Array.isArray(data) ? data : []); setLoading(false) })
      .catch(() => setLoading(false))
    load()
    const id = setInterval(load, 5000)
    return () => clearInterval(id)
  }, [])

  if (loading) return <p className="center">Loading notifications...</p>

  return (
    <div>
      <h1>Notifications</h1>
      <p className="muted">{notifications.length} notification{notifications.length !== 1 ? 's' : ''} — auto-refreshes every 5s</p>
      {notifications.length === 0 ? (
        <p className="center">No notifications yet. Place an order to trigger one.</p>
      ) : (
        <div className="notif-list">
          {[...notifications].reverse().map((n, i) => (
            <div key={i} className="notif-item">
              <div className="notif-header">
                <span className={`status status-${n.status?.toLowerCase()}`}>{n.status}</span>
                <span className="muted">Order {n.orderId?.slice(0, 8)}...</span>
              </div>
              <p>{n.quantity}x {n.goodsType?.replace(/_/g, ' ')}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
