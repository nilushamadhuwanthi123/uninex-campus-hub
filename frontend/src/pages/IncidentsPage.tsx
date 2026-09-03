import { useEffect, useState, type FormEvent } from 'react'
import { api, ApiError } from '../lib/api'
import type { Incident, Resource } from '../lib/types'
import StatusBadge from '../components/StatusBadge'

export default function IncidentsPage() {
  const [incidents, setIncidents] = useState<Incident[] | null>(null)
  const [resources, setResources] = useState<Resource[]>([])
  const [error, setError] = useState<string | null>(null)
  const [formError, setFormError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  function reload() {
    api
      .get<Incident[]>('/api/incidents')
      .then(setIncidents)
      .catch((err: unknown) =>
        setError(err instanceof ApiError ? err.message : 'Could not load incidents.'),
      )
  }

  useEffect(() => {
    reload()
    api.get<Resource[]>('/api/resources').then(setResources).catch(() => {})
  }, [])

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setFormError(null)
    setSubmitting(true)
    const form = new FormData(event.currentTarget)
    try {
      await api.post('/api/incidents', {
        resourceId: form.get('resourceId'),
        title: form.get('title'),
        description: form.get('description'),
        severity: form.get('severity'),
        reporterName: form.get('reporterName'),
        reporterEmail: form.get('reporterEmail'),
      })
      event.currentTarget.reset()
      reload()
    } catch (err) {
      if (err instanceof ApiError && err.status === 401) {
        setFormError('You need to sign in with Google before reporting an incident.')
      } else if (err instanceof ApiError) {
        setFormError(err.message)
      } else {
        setFormError('Something went wrong. Please try again.')
      }
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="grid gap-6 lg:grid-cols-[1fr_320px]">
      <div>
        {error && <p className="text-red-300">{error}</p>}
        {!error && !incidents && <p className="text-cream/60">Loading incidents...</p>}
        {incidents && incidents.length === 0 && (
          <p className="text-cream/60">No incidents reported — hopefully it stays that way.</p>
        )}
        {incidents && incidents.length > 0 && (
          <div className="space-y-3">
            {incidents.map((i) => (
              <div key={i.id} className="rounded-lg border border-gold/20 bg-navy-soft p-4">
                <div className="flex items-center justify-between">
                  <span className="font-medium text-cream">{i.title}</span>
                  <StatusBadge status={i.status} />
                </div>
                <p className="mt-1 text-sm text-cream/70">{i.description}</p>
                <p className="mt-1 text-xs text-cream/50">
                  {i.severity} · {i.resourceId}
                  {i.assignedTechnician ? ` · assigned to ${i.assignedTechnician}` : ''}
                </p>
              </div>
            ))}
          </div>
        )}
      </div>

      <form onSubmit={handleSubmit} className="h-fit rounded-lg border border-gold/20 bg-navy-soft p-4 space-y-3">
        <h3 className="font-semibold text-cream">Report an incident</h3>
        <select name="resourceId" required className="w-full rounded bg-navy border border-gold/20 p-2 text-sm">
          <option value="">Select a resource</option>
          {resources.map((r) => (
            <option key={r.id} value={r.id}>{r.name}</option>
          ))}
        </select>
        <input name="title" placeholder="Short title" required className="w-full rounded bg-navy border border-gold/20 p-2 text-sm" />
        <textarea name="description" placeholder="What's wrong?" required rows={3} className="w-full rounded bg-navy border border-gold/20 p-2 text-sm" />
        <select name="severity" required className="w-full rounded bg-navy border border-gold/20 p-2 text-sm">
          <option value="LOW">Low</option>
          <option value="MEDIUM">Medium</option>
          <option value="HIGH">High</option>
          <option value="CRITICAL">Critical</option>
        </select>
        <input name="reporterName" placeholder="Your name" required className="w-full rounded bg-navy border border-gold/20 p-2 text-sm" />
        <input name="reporterEmail" type="email" placeholder="Your email" required className="w-full rounded bg-navy border border-gold/20 p-2 text-sm" />
        {formError && <p className="text-xs text-red-300">{formError}</p>}
        <button type="submit" disabled={submitting} className="w-full rounded bg-gold py-2 text-sm font-medium text-navy disabled:opacity-50">
          {submitting ? 'Submitting...' : 'Report incident'}
        </button>
      </form>
    </div>
  )
}
