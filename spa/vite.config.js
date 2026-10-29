import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import fs from 'fs'

function getApiGatewayId() {
  try {
    const env = fs.readFileSync('../ministack/cognito-config.env', 'utf8')
    const match = env.match(/API_GATEWAY_ID=(.+)/)
    return match ? match[1].trim() : null
  } catch { return null }
}

const apiGatewayId = getApiGatewayId()
const apiTarget = apiGatewayId
  ? `http://localhost:4566/restapis/${apiGatewayId}/prod/_user_request_`
  : 'http://localhost:4566'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: apiTarget,
        rewrite: p => p.replace(/^\/api/, ''),
        changeOrigin: true
      }
    }
  }
})
