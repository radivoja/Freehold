import { useState, useEffect } from 'react'
import { marketplaceApi } from '../api'

export default function Marketplace() {
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    marketplaceApi.getAll()
      .then(data => { setItems(Array.isArray(data) ? data : []); setLoading(false) })
      .catch(e => { setError(e.message); setLoading(false) })
  }, [])

  if (loading) return <p className="center">Loading market data...</p>
  if (error) return <p className="center error">Error: {error}</p>

  return (
    <div>
      <h1>Marketplace</h1>
      <p className="muted">Current market prices and stock levels</p>
      <div className="table-wrap">
        <table>
          <thead>
            <tr><th>Product</th><th>Price</th><th>Quantity</th><th>Last updated</th></tr>
          </thead>
          <tbody>
            {items.map((m, i) => (
              <tr key={m.goodsType || i}>
                <td>{m.goodsType?.replace(/_/g, ' ')}</td>
                <td className="num">{m.price?.toFixed(2)} RSD</td>
                <td className="num">{m.quantity}</td>
                <td className="muted">{m.updateTimestamp ? new Date(m.updateTimestamp).toLocaleString() : '—'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
