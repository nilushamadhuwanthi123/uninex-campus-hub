/**
 * Where the signed-in user's API token lives on the client.
 *
 * The deployed frontend (GitHub Pages) and the API (Render) are different
 * sites, so a session cookie from the API is a third-party cookie to this
 * page and browsers block it -- login completed but every request after it
 * arrived anonymous. The backend therefore hands out a bearer token on
 * successful login instead, and this module is the one place that knows
 * where it is kept and how it gets there.
 */

const STORAGE_KEY = 'uninex.apiToken'

/**
 * The backend redirects back here after Google login with the token in the
 * URL fragment (`#token=...`) -- the fragment is never sent to any server,
 * so the token does not end up in access logs or Referer headers on the way.
 *
 * Call this once, as early as possible, before anything reads the token.
 * It moves the token into storage and strips it from the address bar so a
 * copied URL does not carry a working credential.
 */
export function captureTokenFromUrl(): void {
  const hash = window.location.hash
  if (!hash.startsWith('#token=')) {
    return
  }

  const token = decodeURIComponent(hash.slice('#token='.length))
  if (token) {
    setToken(token)
  }

  // Drop the fragment without adding a history entry, so Back does not
  // return to a URL that still contains the token.
  window.history.replaceState(null, '', window.location.pathname + window.location.search)
}

export function getToken(): string | null {
  try {
    return window.localStorage.getItem(STORAGE_KEY)
  } catch {
    // Storage can throw outright when a browser is set to block site data.
    // Being unable to remember a login is not a reason to break the page.
    return null
  }
}

export function setToken(token: string): void {
  try {
    window.localStorage.setItem(STORAGE_KEY, token)
  } catch {
    // Ignored for the same reason as above: the session simply will not
    // survive a reload.
  }
}

export function clearToken(): void {
  try {
    window.localStorage.removeItem(STORAGE_KEY)
  } catch {
    // Ignored -- see above.
  }
}
