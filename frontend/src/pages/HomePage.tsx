import TiltCard from '../components/TiltCard'
import heroVideo from '../assets/hero/hero-bg.mp4'
import heroPoster from '../assets/hero/hero-poster.jpg'

const FEATURES = [
  {
    title: 'Book real resources',
    body: 'Halls, labs and equipment, with live availability and admin approval.',
  },
  {
    title: 'Report incidents',
    body: 'Flag a fault, track it from report to resolution, no back-and-forth emails.',
  },
  {
    title: 'Rate what you use',
    body: 'Leave a review, see a live average rating computed from real feedback.',
  },
  {
    title: 'Know the moment it changes',
    body: 'Real-time notifications the moment a booking is approved or an incident is resolved.',
  },
]

export default function HomePage({ onNavigate }: { onNavigate?: (tab: string) => void }) {
  return (
    <div className="space-y-10">
      <div className="relative overflow-hidden rounded-lg border border-gold/20">
        <video
          className="h-72 w-full object-cover sm:h-96"
          src={heroVideo}
          poster={heroPoster}
          autoPlay
          loop
          muted
          playsInline
        />
        <div className="absolute inset-0 bg-gradient-to-t from-navy via-navy/60 to-navy/10" />
        <div className="absolute inset-0 flex flex-col items-start justify-end p-6 sm:p-10">
          <p className="text-xs uppercase tracking-[0.3em] text-gold">Uninex</p>
          <h2 className="mt-2 max-w-lg text-2xl font-semibold text-cream sm:text-3xl">
            Book campus resources, report issues, and see it all update live.
          </h2>
          <p className="mt-2 max-w-md text-sm text-cream/70">
            A real Spring Boot + MongoDB backend behind a real React frontend --
            every page on this site talks to the live API, nothing here is mocked.
          </p>
          <div className="mt-5 flex gap-3">
            <button
              onClick={() => onNavigate?.('resources')}
              className="rounded bg-gold px-4 py-2 text-sm font-medium text-navy"
            >
              Browse resources
            </button>
            <button
              onClick={() => onNavigate?.('dashboard')}
              className="rounded border border-gold/40 px-4 py-2 text-sm font-medium text-cream"
            >
              Go to dashboard
            </button>
          </div>
        </div>
      </div>

      <div className="grid gap-4 sm:grid-cols-2">
        {FEATURES.map((f) => (
          <TiltCard key={f.title} maxTilt={6}>
            <div className="h-full rounded-lg border border-gold/20 bg-navy-soft p-5">
              <h3 className="font-semibold text-gold">{f.title}</h3>
              <p className="mt-1 text-sm text-cream/70">{f.body}</p>
            </div>
          </TiltCard>
        ))}
      </div>
    </div>
  )
}
