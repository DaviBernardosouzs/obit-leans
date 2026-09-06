import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'

const securityHeaders = {
  'X-Content-Type-Options': 'nosniff',
  'Referrer-Policy': 'no-referrer',
  'Permissions-Policy': 'camera=(), microphone=(), geolocation=(), payment=()',
}

const developmentCsp = "default-src 'self'; base-uri 'self'; object-src 'none'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'; img-src 'self' blob: data:; connect-src 'self' http://localhost:8081 ws://localhost:* ws://127.0.0.1:*; form-action 'self'"
const productionCsp = "default-src 'self'; base-uri 'self'; object-src 'none'; script-src 'self'; style-src 'self'; img-src 'self' blob: data:; connect-src 'self' http://localhost:8081; form-action 'self'; frame-ancestors 'none'"

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, '.', '')
  const backendUrl = env.VITE_API_BASE_URL || 'http://127.0.0.1:8081'

  return {
    plugins: [react()],
    server: {
      port: 5173,
      headers: {
        ...securityHeaders,
        'Content-Security-Policy': developmentCsp,
      },
      proxy: {
        '/api': {
          target: backendUrl,
          changeOrigin: true,
        },
      },
    },
    preview: {
      headers: {
        ...securityHeaders,
        'X-Frame-Options': 'DENY',
        'Cross-Origin-Opener-Policy': 'same-origin',
        'Content-Security-Policy': productionCsp,
      },
    },
  }
})
