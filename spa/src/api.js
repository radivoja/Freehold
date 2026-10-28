const BASE = '/api'

async function request(path, options = {}) {
  const res = await fetch(`${BASE}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  })
  if (!res.ok) throw new Error(`${res.status} ${res.statusText}`)
  return res.json()
}

export const goodsApi = {
  getAll: () => request('/production/goods'),
  getById: (id) => request(`/production/goods/${id}`),
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
