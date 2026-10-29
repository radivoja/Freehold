import { getIdToken } from './auth/authService'

const BASE = '/api'

async function request(path, options = {}) {
  const token = await getIdToken()
  const headers = { 'Content-Type': 'application/json' }
  if (token) headers['Authorization'] = `Bearer ${token}`

  const res = await fetch(`${BASE}${path}`, { headers, ...options })
  if (!res.ok) throw new Error(`${res.status} ${res.statusText}`)
  if (res.status === 204) return null
  return res.json()
}

export const goodsApi = {
  getAll: () => request('/production/goods'),
  getById: (id) => request(`/production/goods/${id}`),
  create: (goods) => request('/production/goods', { method: 'POST', body: JSON.stringify(goods) }),
  update: (id, goods) => request(`/production/goods/${id}`, { method: 'PUT', body: JSON.stringify(goods) }),
  delete: (id) => request(`/production/goods/${id}`, { method: 'DELETE' }),
}

export const marketplaceApi = {
  getAll: () => request('/marketplace/market'),
  getByType: (type) => request(`/marketplace/market/${type}`),
}

export const orderApi = {
  getAll: () => request('/order/orders'),
  getById: (id) => request(`/order/orders/${id}`),
  create: (order) => request('/order/orders', { method: 'POST', body: JSON.stringify(order) }),
  updateStatus: (id, status) => request(`/order/orders/${id}/status?status=${status}`, { method: 'PATCH' }),
}

export const notificationApi = {
  getAll: () => request('/notification/notifications'),
  getCount: () => request('/notification/notifications/count'),
}
