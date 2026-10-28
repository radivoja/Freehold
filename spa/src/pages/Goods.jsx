import { useState, useEffect } from 'react'
import { goodsApi } from '../api'

const CATEGORIES = ['ALL', 'RAKIJA', 'JUICE', 'PRESERVE', 'DAIRY', 'MEAT', 'HONEY', 'FRUIT', 'VEGETABLE']

export default function Goods() {
  const [goods, setGoods] = useState([])
  const [filter, setFilter] = useState('ALL')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    goodsApi.getAll()
      .then(data => { setGoods(Array.isArray(data) ? data : []); setLoading(false) })
      .catch(e => { setError(e.message); setLoading(false) })
  }, [])

  const filtered = filter === 'ALL' ? goods : goods.filter(g => g.category === filter)
  const display = filtered.slice(0, 50)

  if (loading) return <p className="center">Loading goods...</p>
  if (error) return <p className="center error">Error: {error}</p>

  return (
    <div>
      <h1>Production goods</h1>
      <div className="filter-bar">
        {CATEGORIES.map(c => (
          <button key={c} className={`filter-btn ${filter === c ? 'active' : ''}`} onClick={() => setFilter(c)}>
            {c.charAt(0) + c.slice(1).toLowerCase()}
          </button>
        ))}
      </div>
      <p className="muted">Showing {display.length} of {filtered.length} items</p>
      <div className="table-wrap">
        <table>
          <thead>
            <tr><th>Name</th><th>Category</th><th>Subcategory</th><th>Price</th><th>Qty</th><th>Origin</th></tr>
          </thead>
          <tbody>
            {display.map((g, i) => (
              <tr key={g.id || i}>
                <td>{g.name}</td>
                <td><span className="tag">{g.category}</span></td>
                <td>{g.subcategory}</td>
                <td className="num">{g.price?.toFixed(2)} RSD</td>
                <td className="num">{g.quantity}</td>
                <td>{g.origin || '—'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
