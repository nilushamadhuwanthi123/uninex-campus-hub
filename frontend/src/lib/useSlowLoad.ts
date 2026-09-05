import { useEffect, useState } from 'react'

/**
 * The Render free-tier backend spins down after ~15 minutes of
 * inactivity and takes 30-60s to wake back up on the next request
 * (see Render's own "free instance will spin down" notice). A first
 * visitor after a quiet spell would otherwise stare at a bare
 * "Loading..." for up to a minute with no explanation.
 *
 * This hook flips to true once `loading` has been true for longer
 * than a normal request should take, so callers can swap in a
 * friendlier "the server is waking up" message instead of leaving
 * the user wondering if the page is broken.
 */
export function useSlowLoad(loading: boolean, delayMs = 4000): boolean {
  const [slow, setSlow] = useState(false)

  useEffect(() => {
    if (!loading) {
      setSlow(false)
      return
    }
    const timer = setTimeout(() => setSlow(true), delayMs)
    return () => clearTimeout(timer)
  }, [loading, delayMs])

  return slow
}
