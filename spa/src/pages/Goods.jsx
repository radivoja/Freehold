import { useState, useEffect } from 'react'
import { goodsApi } from '../api'
import { useAuth } from '../auth/AuthContext'

const CATEGORIES = ['ALL', 'RAKIJA', 'JUICE', 'PRESERVE', 'DAIRY', 'MEAT', 'HONEY', 'FRUIT', 'VEGETABLE']

const SUBCATEGORIES = {
  RAKIJA: ['SLJIVOVICA', 'LOZOVACA', 'DUNJEVACA', 'KAJSIJEVACA', 'VILJAMOVKA', 'KLEKOVACA'],
  JUICE: ['APPLE_JUICE', 'PEAR_JUICE', 'RASPBERRY_JUICE', 'SOUR_CHERRY_JUICE'],
  PRESERVE: ['AJVAR', 'PINDJUR', 'LJUTENICA', 'SLATKO', 'PEKMEZ'],
  DAIRY: ['KAJMAK', 'PRLJO', 'ZLATIBORSKI_SIR'],
  MEAT: ['UZICKA_PRSUTA', 'SREMSKA_KOBASICA', 'DIMLJENO_MESO', 'SUDZUK'],
  HONEY: ['LIPOV_MED', 'BAGREMOV_MED', 'LIVADSKI_MED'],
  FRUIT: ['PLUMS', 'RASPBERRIES', 'QUINCE', 'APPLES', 'WALNUTS'],
  VEGETABLE: ['PEPPERS', 'TOMATOES', 'CUCUMBERS', 'POTATOES'],
}

const EMPTY_FORM = {
  name: '', description: '', subcategory: 'SLJIVOVICA', price: '', quantity: '', weight: '', origin: '', harvestDate: ''
}

export default function Goods() {
  const { role } = useAuth()
  const [goods, setGoods] = useState([])
  const [filter, setFilter] = useState('ALL')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState(EMPTY_FORM)
  const [submitting, setSubmitting] = useState(false)

  const canManage = role === 'LANDOWNER' || role === 'ADMIN'

  useEffect(() => { loadGoods() }, [])

  function loadGoods() {
    setLoading(true)
    goodsApi.getAll()
      .then(data => { setGoods(Array.isArray(data) ? data : []); setLoading(false) })
      .catch(e => { setError(e.message); setLoading(false) })
  }

  async function handleCreate(e) {
    e.preventDefault()
    setSubmitting(true)
    try {
      await goodsApi.create({
        ...form,
        price: parseFloat(form.price),
        quantity: parseInt(form.quantity),
        weight: form.weight ? parseFloat(form.weight) : null,
        harvestDate: form.harvestDate || null,
      })
      setForm(EMPTY_FORM)
      setShowForm(false)
      loadGoods()
    } catch (err) {
      setError(err.message)
    } finally {
      setSubmitting(false)
    }
  }

  async function handleDelete(id) {
    if (!confirm('Delete this good?')) return
    try {
      await goodsApi.delete(id)
      setGoods(goods.filter(g => g.id !== id))
    } catch (err) {
      setError(err.message)
    }
  }

  function updateField(field, value) {
    setForm(prev => ({ ...prev, [field]: value }))
  }

  const filtered = filter === 'ALL' ? goods : goods.filter(g => g.category === filter)
  const display = filtered.slice(0, 50)

  if (loading) return <p className="center">Loading goods...</p>
  if (error) return <p className="center error">Error: {error}</p>

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h1>Production goods</h1>
        {canManage && !showForm && (
          <button className="btn-primary" onClick={() => setShowForm(true)}>+ New Good</button>
        )}
      </div>

      {showForm && (
        <form onSubmit={handleCreate} className="create-form">
          <h3>Create new good</h3>
          <div className="form-grid">
            <input placeholder="Name" required value={form.name}
              onChange={e => updateField('name', e.target.value)} />
            <input placeholder="Description" value={form.description}
              onChange={e => updateField('description', e.target.value)} />
            <select value={form.subcategory} onChange={e => updateField('subcategory', e.target.value)}>
              {Object.entries(SUBCATEGORIES).map(([cat, subs]) => (
                <optgroup key={cat} label={cat}>
                  {subs.map(s => <option key={s} value={s}>{s.replace(/_/g, ' ')}</option>)}
                </optgroup>
              ))}
            </select>
            <input type="number" step="0.01" placeholder="Price (RSD)" required value={form.price}
              onChange={e => updateField('price', e.target.value)} />
            <input type="number" placeholder="Quantity" required value={form.quantity}
              onChange={e => updateField('quantity', e.target.value)} />
            <input type="number" step="0.01" placeholder="Weight (kg)" value={form.weight}
              onChange={e => updateField('weight', e.target.value)} />
            <input placeholder="Origin" value={form.origin}
              onChange={e => updateField('origin', e.target.value)} />
            <input type="date" placeholder="Harvest date" value={form.harvestDate}
              onChange={e => updateField('harvestDate', e.target.value)} />
          </div>
          <div className="form-actions">
            <button type="submit" className="btn-primary" disabled={submitting}>
              {submitting ? 'Creating...' : 'Create'}
            </button>
            <button type="button" className="btn-secondary" onClick={() => { setShowForm(false); setForm(EMPTY_FORM) }}>
              Cancel
            </button>
          </div>
        </form>
      )}

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
            <tr>
              <th>Name</th><th>Category</th><th>Subcategory</th><th>Price</th><th>Qty</th><th>Origin</th>
              {canManage && <th>Actions</th>}
            </tr>
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
                {canManage && (
                  <td>
                    <button className="btn-danger-sm" onClick={() => handleDelete(g.id)}>Delete</button>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
