// Thin fetch wrapper around the real backend. In dev, Vite proxies
// "/api" to the backend (see vite.config.ts) so relative paths work.
// In production the frontend (GitHub Pages) and the API (Render) are
// on different origins, so VITE_API_BASE_URL (set at build time, see
// the deploy workflow) is prefixed onto every request instead.

export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? ''

export class ApiError extends Error {
  status: number

  constructor(status: number, message: string) {
    super(message)
    this.status = status
  }
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    credentials: 'include',
    headers: { 'Content-Type': 'application/json', ...options.headers },
    ...options,
  })

  if (response.status === 204) {
    return undefined as T
  }

  const text = await response.text()
  const data = text ? JSON.parse(text) : undefined

  if (!response.ok) {
    throw new ApiError(response.status, typeof data === 'string' ? data : response.statusText)
  }

  return data as T
}

export const api = {
  get: <T>(path: string) => request<T>(path),
  post: <T>(path: string, body?: unknown) =>
    request<T>(path, { method: 'POST', body: body ? JSON.stringify(body) : undefined }),
  del: <T>(path: string) => request<T>(path, { method: 'DELETE' }),
}
