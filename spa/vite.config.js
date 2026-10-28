import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/api/marketplace': { target: 'http://localhost:8091', rewrite: p => p.replace('/api/marketplace', '') },
      '/api/order': { target: 'http://localhost:8093', rewrite: p => p.replace('/api/order', '') },
      '/api/notification': { target: 'http://localhost:8092', rewrite: p => p.replace('/api/notification', '') },
      '/api/production': { target: 'http://localhost:8090', rewrite: p => p.replace('/api/production', '') },
    }
  }
})
