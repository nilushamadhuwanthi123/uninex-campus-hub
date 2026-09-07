import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import { captureTokenFromUrl } from './lib/auth'

// Runs before the first render so the very first /api/auth/me call
// already carries the token the login redirect just delivered.
captureTokenFromUrl()

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
