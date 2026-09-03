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
      <div className="relative left-1/2 right-1/2 -mx-[50vw] w-screen overflow-hidden">
        <video
          className="h-[58vh] min-h-[380px] w-full object-cover object-center sm:h-[72vh]"
          src={heroVideo}
          poster={heroPoster}
          autoPlay
          loop
          muted
          playsInline
        />
        <div className="absolute inset-0 bg-gradient-to-t from-navy via-navy/70 to-navy/20" />
        <div className="absolute inset-0 bg-gradient-to-r from-navy/70 via-navy/10 to-transparent" />
        <div className="absolute inset-0 flex flex-col items-start justify-end px-6 pb-10 sm:px-16 sm:pb-16">
          <p className="text-xs uppercase tracking-[0.3em] text-gold">Uninex</p>
          <h2 className="mt-2 max-w-xl text-3xl font-semibold text-cream sm:text-4xl">
            Book campus resources, report issues, and see it all update live.
          </h2>
          <p className="mt-3 max-w-lg text-sm text-cream/70 sm:text-base">
            A real Spring Boot + MongoDB backend behind a real React frontend --
            every page on this site talks to the live API, nothing here is mocked.
          </p>
          <div className="mt-6 flex gap-3">
            <button
              onClick={() => onNavigate?.('resources')}
              className="rounded bg-gold px-5 py-2.5 text-sm font-medium text-navy"
            >
              Browse resources
            </button>
            <button
              onClick={() => onNavigate?.('dashboard')}
              className="rounded border border-gold/40 px-5 py-2.5 text-sm font-medium text-cream"
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
