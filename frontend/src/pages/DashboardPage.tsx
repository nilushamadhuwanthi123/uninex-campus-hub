import { useEffect, useState } from 'react'
import { api, ApiError } from '../lib/api'
import TiltCard from '../components/TiltCard'
import type { AnalyticsSummary, CurrentUser } from '../lib/types'

export default function DashboardPage() {
  const [user, setUser] = useState<CurrentUser | null | undefined>(undefined) // undefined = loading
  const [summary, setSummary] = useState<AnalyticsSummary | null>(null)
  const [summaryError, setSummaryError] = useState<string | null>(null)

  useEffect(() => {
    api
      .get<CurrentUser>('/api/auth/me')
      .then(setUser)
      .catch(() => setUser(null))
  }, [])

  const isStaff = user?.roles?.some((r) => r.authority === 'ROLE_STAFF' || r.authority === 'ROLE_ADMIN')

  useEffect(() => {
    if (!isStaff) return
    api
      .get<AnalyticsSummary>('/api/analytics/summary')
      .then(setSummary)
      .catch((err: unknown) =>
        setSummaryError(err instanceof ApiError ? err.message : 'Could not load analytics.'),
      )
  }, [isStaff])

  if (user === undefined) {
    return <p className="text-cream/60">Loading...</p>
  }

  if (user === null) {
    return (
      <TiltCard maxTilt={6} className="mx-auto max-w-md animate-fade-in-up">
        <div className="rounded-lg border border-gold/20 bg-navy-soft p-6 text-center">
          <p className="text-cream/70">You're not signed in.</p>
          <a
            href="/oauth2/authorization/google"
            className="mt-4 inline-block rounded bg-gold px-4 py-2 text-sm font-medium text-navy"
          >
            Sign in with Google
          </a>
        </div>
      </TiltCard>
    )
  }

  return (
    <div className="space-y-6">
      <TiltCard maxTilt={4} className="animate-fade-in-up">
        <div className="rounded-lg border border-gold/20 bg-navy-soft p-4">
          <p className="text-cream">Signed in as <span className="font-semibold">{user.name}</span></p>
          <p className="text-xs text-cream/60">{user.email}</p>
          <p className="mt-1 text-xs uppercase tracking-widest text-gold">
            {user.roles.map((r) => r.authority.replace('ROLE_', '')).join(', ')}
          </p>
        </div>
      </TiltCard>

      {isStaff && (
        <div>
          <h3 className="mb-3 font-semibold text-cream">Usage insights</h3>
          {summaryError && <p className="text-red-300">{summaryError}</p>}
          {!summaryError && !summary && <p className="text-cream/60">Loading analytics...</p>}
          {summary && (
            <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
              <Stat label="Resources" value={summary.totalResources} delay={0} />
              <Stat label="Bookings" value={summary.totalBookings} delay={60} />
              <Stat label="Incidents" value={summary.totalIncidents} delay={120} />
              <Stat
                label="Avg. rating"
                value={summary.totalReviews === 0 ? '—' : summary.overallAverageRating.toFixed(1)}
                delay={180}
              />
              <Stat
                label="Avg. resolution"
                value={
                  summary.averageIncidentResolutionMinutes === null
                    ? '—'
                    : `${Math.round(summary.averageIncidentResolutionMinutes)}m`
                }
                delay={240}
              />
            </div>
          )}
        </div>
      )}
    </div>
  )
}

function Stat({ label, value, delay = 0 }: { label: string; value: string | number; delay?: number }) {
  return (
    <TiltCard maxTilt={12} className="animate-fade-in-up" style={{ animationDelay: `${delay}ms` }}>
      <div className="rounded-lg border border-gold/20 bg-navy-soft p-4 text-center">
        <div className="text-2xl font-bold text-gold">{value}</div>
        <div className="mt-1 text-xs uppercase tracking-widest text-cream/60">{label}</div>
      </div>
    </TiltCard>
  )
}
