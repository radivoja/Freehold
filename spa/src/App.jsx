import { Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar'
import Home from './pages/Home'
import Goods from './pages/Goods'
import Marketplace from './pages/Marketplace'
import Orders from './pages/Orders'
import Notifications from './pages/Notifications'

export default function App() {
  return (
    <div className="app">
      <Navbar />
      <main className="page">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/goods" element={<Goods />} />
          <Route path="/marketplace" element={<Marketplace />} />
          <Route path="/orders" element={<Orders />} />
          <Route path="/notifications" element={<Notifications />} />
        </Routes>
      </main>
    </div>
  )
}
