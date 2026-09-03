import { useEffect, useState } from 'react'
import { api, ApiError } from '../lib/api'
import type { Resource } from '../lib/types'

export default function ResourcesPage() {
  const [resources, setResources] = useState<Resource[] | null>(null)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    api
      .get<Resource[]>('/api/resources')
      .then(setResources)
      .catch((err: unknown) =>
        setError(err instanceof ApiError ? err.message : 'Could not load resources.'),
      )
  }, [])

  if (error) {
    return <p className="text-red-300">{error}</p>
  }
  if (!resources) {
    return <p className="text-cream/60">Loading resources...</p>
  }
  if (resources.length === 0) {
    return (
      <p className="text-cream/60">
        No resources yet — an admin adds them via <code>POST /api/resources</code>.
      </p>
    )
  }

  return (
    <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
      {resources.map((r) => (
        <div key={r.id} className="rounded-lg border border-gold/20 bg-navy-soft p-4">
          <div className="flex items-center justify-between">
            <h3 className="font-semibold text-cream">{r.name}</h3>
            <span className="text-xs uppercase tracking-widest text-gold">{r.type}</span>
          </div>
          {r.description && <p className="mt-2 text-sm text-cream/70">{r.description}</p>}
          <div className="mt-3 flex items-center justify-between text-xs text-cream/60">
            <span>Capacity: {r.capacity}</span>
            <span>{r.active ? 'Active' : 'Inactive'}</span>
          </div>
          {r.facilities?.length > 0 && (
            <p className="mt-2 text-xs text-cream/50">{r.facilities.join(' · ')}</p>
          )}
        </div>
      ))}
    </div>
  )
}
