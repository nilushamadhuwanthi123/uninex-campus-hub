import { useEffect, useState, type FormEvent } from 'react'
import { api, ApiError } from '../lib/api'
import type { Booking, Resource } from '../lib/types'
import StatusBadge from '../components/StatusBadge'

export default function BookingsPage() {
  const [bookings, setBookings] = useState<Booking[] | null>(null)
  const [resources, setResources] = useState<Resource[]>([])
  const [error, setError] = useState<string | null>(null)
  const [formError, setFormError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  function reload() {
    api
      .get<Booking[]>('/api/bookings')
      .then(setBookings)
      .catch((err: unknown) =>
        setError(err instanceof ApiError ? err.message : 'Could not load bookings.'),
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
      await api.post('/api/bookings', {
        resourceId: form.get('resourceId'),
        seatNumbers: [],
        startTime: new Date(String(form.get('startTime'))).toISOString(),
        endTime: new Date(String(form.get('endTime'))).toISOString(),
        requesterName: form.get('requesterName'),
        requesterEmail: form.get('requesterEmail'),
      })
      event.currentTarget.reset()
      reload()
    } catch (err) {
      if (err instanceof ApiError && err.status === 401) {
        setFormError('You need to sign in with Google before requesting a booking.')
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
        {!error && !bookings && <p className="text-cream/60">Loading bookings...</p>}
        {bookings && bookings.length === 0 && (
          <p className="text-cream/60">No bookings yet — be the first to request one.</p>
        )}
        {bookings && bookings.length > 0 && (
          <div className="space-y-3">
            {bookings.map((b) => (
              <div key={b.id} className="rounded-lg border border-gold/20 bg-navy-soft p-4">
                <div className="flex items-center justify-between">
                  <span className="font-medium text-cream">{b.resourceId}</span>
                  <StatusBadge status={b.status} />
                </div>
                <p className="mt-1 text-xs text-cream/60">
                  {new Date(b.startTime).toLocaleString()} → {new Date(b.endTime).toLocaleString()}
                </p>
                <p className="mt-1 text-xs text-cream/50">{b.requesterName}</p>
                {b.ticketCode && (
                  <p className="mt-1 text-xs text-gold">Ticket: {b.ticketCode}</p>
                )}
              </div>
            ))}
          </div>
        )}
      </div>

      <form onSubmit={handleSubmit} className="h-fit rounded-lg border border-gold/20 bg-navy-soft p-4 space-y-3">
        <h3 className="font-semibold text-cream">Request a booking</h3>
        <select name="resourceId" required className="w-full rounded bg-navy border border-gold/20 p-2 text-sm">
          <option value="">Select a resource</option>
          {resources.map((r) => (
            <option key={r.id} value={r.id}>{r.name}</option>
          ))}
        </select>
        <input name="startTime" type="datetime-local" required className="w-full rounded bg-navy border border-gold/20 p-2 text-sm" />
        <input name="endTime" type="datetime-local" required className="w-full rounded bg-navy border border-gold/20 p-2 text-sm" />
        <input name="requesterName" placeholder="Your name" required className="w-full rounded bg-navy border border-gold/20 p-2 text-sm" />
        <input name="requesterEmail" type="email" placeholder="Your email" required className="w-full rounded bg-navy border border-gold/20 p-2 text-sm" />
        {formError && <p className="text-xs text-red-300">{formError}</p>}
        <button type="submit" disabled={submitting} className="w-full rounded bg-gold py-2 text-sm font-medium text-navy disabled:opacity-50">
          {submitting ? 'Submitting...' : 'Request booking'}
        </button>
      </form>
    </div>
  )
}
