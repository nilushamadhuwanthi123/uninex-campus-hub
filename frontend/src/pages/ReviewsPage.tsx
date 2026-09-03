import { useEffect, useState, type FormEvent } from 'react'
import { api, ApiError } from '../lib/api'
import type { Resource, Review, ResourceRating } from '../lib/types'

export default function ReviewsPage() {
  const [resources, setResources] = useState<Resource[]>([])
  const [resourceId, setResourceId] = useState('')
  const [reviews, setReviews] = useState<Review[] | null>(null)
  const [rating, setRating] = useState<ResourceRating | null>(null)
  const [formError, setFormError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    api.get<Resource[]>('/api/resources').then((list) => {
      setResources(list)
      if (list.length > 0) setResourceId(list[0].id)
    })
  }, [])

  function reload(id: string) {
    if (!id) return
    api.get<Review[]>(`/api/reviews?resourceId=${id}`).then(setReviews)
    api.get<ResourceRating>(`/api/reviews/summary?resourceId=${id}`).then(setRating)
  }

  useEffect(() => {
    reload(resourceId)
  }, [resourceId])

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setFormError(null)
    setSubmitting(true)
    const form = new FormData(event.currentTarget)
    try {
      await api.post('/api/reviews', {
        resourceId,
        rating: Number(form.get('rating')),
        comment: form.get('comment'),
        reviewerName: form.get('reviewerName'),
        reviewerEmail: form.get('reviewerEmail'),
      })
      event.currentTarget.reset()
      reload(resourceId)
    } catch (err) {
      if (err instanceof ApiError && err.status === 401) {
        setFormError('You need to sign in with Google before leaving a review.')
      } else if (err instanceof ApiError) {
        setFormError(err.message)
      } else {
        setFormError('Something went wrong. Please try again.')
      }
    } finally {
      setSubmitting(false)
    }
  }

  if (resources.length === 0) {
    return <p className="text-cream/60">No resources to review yet.</p>
  }

  return (
    <div className="space-y-4">
      <select
        value={resourceId}
        onChange={(e) => setResourceId(e.target.value)}
        className="rounded bg-navy border border-gold/20 p-2 text-sm"
      >
        {resources.map((r) => (
          <option key={r.id} value={r.id}>{r.name}</option>
        ))}
      </select>

      {rating && (
        <p className="text-sm text-gold">
          {rating.reviewCount === 0
            ? 'No reviews yet for this resource.'
            : `${rating.averageRating.toFixed(1)} / 5 average, from ${rating.reviewCount} review${rating.reviewCount === 1 ? '' : 's'}`}
        </p>
      )}

      <div className="grid gap-6 lg:grid-cols-[1fr_320px]">
        <div className="space-y-3">
          {reviews?.map((r) => (
            <div key={r.id} className="rounded-lg border border-gold/20 bg-navy-soft p-4">
              <div className="flex items-center justify-between">
                <span className="font-medium text-cream">{r.reviewerName}</span>
                <span className="text-gold">{'★'.repeat(r.rating)}{'☆'.repeat(5 - r.rating)}</span>
              </div>
              {r.comment && <p className="mt-1 text-sm text-cream/70">{r.comment}</p>}
            </div>
          ))}
        </div>

        <form onSubmit={handleSubmit} className="h-fit rounded-lg border border-gold/20 bg-navy-soft p-4 space-y-3">
          <h3 className="font-semibold text-cream">Leave a review</h3>
          <select name="rating" required className="w-full rounded bg-navy border border-gold/20 p-2 text-sm">
            {[5, 4, 3, 2, 1].map((n) => (
              <option key={n} value={n}>{n} star{n === 1 ? '' : 's'}</option>
            ))}
          </select>
          <textarea name="comment" placeholder="Comment (optional)" rows={2} className="w-full rounded bg-navy border border-gold/20 p-2 text-sm" />
          <input name="reviewerName" placeholder="Your name" required className="w-full rounded bg-navy border border-gold/20 p-2 text-sm" />
          <input name="reviewerEmail" type="email" placeholder="Your email" required className="w-full rounded bg-navy border border-gold/20 p-2 text-sm" />
          {formError && <p className="text-xs text-red-300">{formError}</p>}
          <button type="submit" disabled={submitting} className="w-full rounded bg-gold py-2 text-sm font-medium text-navy disabled:opacity-50">
            {submitting ? 'Submitting...' : 'Submit review'}
          </button>
        </form>
      </div>
    </div>
  )
}
