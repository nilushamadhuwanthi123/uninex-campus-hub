import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig(({ command }) => ({
  // GitHub Pages serves this repo at /uninex-campus-hub/, not the domain
  // root, so production asset URLs need that prefix. The dev server still
  // serves from / so local development is unaffected.
  base: command === 'build' ? '/uninex-campus-hub/' : '/',
  plugins: [react(), tailwindcss()],
  server: {
    proxy: {
      '/api': 'http://localhost:8080',
      // Spring Security's real OAuth2 login endpoints -- proxied too so
      // "Sign in with Google" works from the Vite dev server without a
      // separate backend origin.
      '/oauth2': 'http://localhost:8080',
      '/login': 'http://localhost:8080',
    },
  },
}))
