import { useState, useEffect } from 'react'
import { orderApi, goodsApi } from '../api'

const DEMO_CUSTOMER = '550e8400-e29b-41d4-a716-446655440000'

export default function Orders() {
  const [orders, setOrders] = useState([])
  const [goods, setGoods] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [submitting, setSubmitting] = useState(false)
  const [form, setForm] = useState({ goodsId: '', quantity: 1, deliveryAddress: '' })

  const loadOrders = () => orderApi.getAll().then(setOrders).catch(() => {})

  useEffect(() => {
    Promise.all([
      orderApi.getAll().catch(() => []),
      goodsApi.getAll().catch(() => []),
    ]).then(([o, g]) => {
      setOrders(Array.isArray(o) ? o : [])
      setGoods(Array.isArray(g) ? g.slice(0, 100) : [])
      setLoading(false)
    }).catch(e => { setError(e.message); setLoading(false) })
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    const selected = goods.find(g => g.id === form.goodsId)
    if (!selected) return
    setSubmitting(true)
    try {
      await orderApi.create({
        customerId: DEMO_CUSTOMER,
        goodsId: selected.id,
        goodsName: selected.name,
        quantity: parseInt(form.quantity),
        totalPrice: selected.price * parseInt(form.quantity),
        deliveryAddress: form.deliveryAddress,
      })
      setForm({ goodsId: '', quantity: 1, deliveryAddress: '' })
      await loadOrders()
    } catch (err) {
      setError(err.message)
    }
    setSubmitting(false)
  }

  if (loading) return <p className="center">Loading...</p>

  return (
    <div>
      <h1>Orders</h1>

      <div className="card form-card">
        <h2>Place an order</h2>
        {error && <p className="error">{error}</p>}
        <form onSubmit={handleSubmit}>
          <label>
            Product
            <select value={form.goodsId} onChange={e => setForm({ ...form, goodsId: e.target.value })} required>
              <option value="">Select a product...</option>
              {goods.map(g => (
                <option key={g.id} value={g.id}>{g.name} — {g.price?.toFixed(2)} RSD</option>
              ))}
            </select>
          </label>
          <label>
            Quantity
            <input type="number" min="1" max="999" value={form.quantity}
              onChange={e => setForm({ ...form, quantity: e.target.value })} required />
          </label>
          <label>
            Delivery address
            <input type="text" value={form.deliveryAddress} placeholder="Belgrade, Serbia"
              onChange={e => setForm({ ...form, deliveryAddress: e.target.value })} required />
          </label>
          <button type="submit" disabled={submitting}>{submitting ? 'Placing...' : 'Place order'}</button>
        </form>
      </div>

      <h2>Order history</h2>
      {orders.length === 0 ? (
        <p className="muted">No orders yet</p>
      ) : (
        <div className="table-wrap">
          <table>
            <thead>
              <tr><th>Product</th><th>Qty</th><th>Total</th><th>Status</th><th>Address</th><th>Created</th></tr>
            </thead>
            <tbody>
              {orders.map(o => (
                <tr key={o.id}>
                  <td>{o.goodsName}</td>
                  <td className="num">{o.quantity}</td>
                  <td className="num">{o.totalPrice?.toFixed(2)} RSD</td>
                  <td><span className={`status status-${o.status?.toLowerCase()}`}>{o.status}</span></td>
                  <td>{o.deliveryAddress}</td>
                  <td className="muted">{new Date(o.createdAt).toLocaleString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
