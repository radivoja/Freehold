import { Link } from 'react-router-dom'

export default function Home() {
  return (
    <div className="home">
      <h1>Freehold</h1>
      <p className="subtitle">Serbian artisan food & spirits marketplace</p>
      <div className="card-grid">
        <Link to="/goods" className="home-card">
          <h2>Production</h2>
          <p>Browse artisan goods from local producers</p>
        </Link>
        <Link to="/marketplace" className="home-card">
          <h2>Marketplace</h2>
          <p>View current market prices and availability</p>
        </Link>
        <Link to="/orders" className="home-card">
          <h2>Orders</h2>
          <p>Place and track your orders</p>
        </Link>
        <Link to="/notifications" className="home-card">
          <h2>Notifications</h2>
          <p>Order updates and alerts</p>
        </Link>
      </div>
    </div>
  )
}
