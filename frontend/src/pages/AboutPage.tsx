import TiltCard from '../components/TiltCard'

const STACK = [
  { label: 'Backend', value: 'Spring Boot 3.5.16 (Java 21), Spring Data MongoDB, Spring Security, OAuth2 Client' },
  { label: 'Frontend', value: 'React 19 + TypeScript, Vite, Tailwind CSS 4' },
  { label: 'Database', value: 'MongoDB (embedded for tests, Docker Compose for local dev)' },
  { label: 'Testing', value: 'JUnit 5, Mockito, Spring @DataMongoTest / @SpringBootTest' },
]

const MILESTONES = [
  'Resource & seat management (admin CRUD)',
  'Booking system with time slots and full-hall reservation',
  'Admin approval workflow with QR ticket generation',
  'Incident ticket system with technician assignment',
  'Google OAuth2 login and role-based access',
  'Review, rating and feedback system',
  'Analytics dashboard for usage insights',
  'Real-time notifications',
  'Real React frontend wired to every live endpoint',
]

export default function AboutPage() {
  return (
    <div className="space-y-10">
      <div>
        <p className="text-xs uppercase tracking-[0.3em] text-gold">About</p>
        <h2 className="mt-2 text-2xl font-semibold text-cream sm:text-3xl">
          A real campus booking system, built solo end to end
        </h2>
        <p className="mt-3 max-w-2xl text-sm leading-relaxed text-cream/70">
          Uninex Campus Hub is a solo project by Nilusha Madhuwanthi — the same problem
          space as a typical university resource-booking system (seat and hall booking,
          admin approval, incident tracking), designed and implemented from scratch,
          feature by feature, through real GitHub issues, branches and pull requests.
          Nothing on this site is a static mockup: every page talks to the live Spring
          Boot API backed by a real MongoDB instance.
        </p>
      </div>

      <div className="grid gap-4 sm:grid-cols-2">
        {STACK.map((s) => (
          <TiltCard key={s.label} maxTilt={5}>
            <div className="h-full rounded-lg border border-gold/20 bg-navy-soft p-5">
              <h3 className="font-semibold text-gold">{s.label}</h3>
              <p className="mt-1 text-sm text-cream/70">{s.value}</p>
            </div>
          </TiltCard>
        ))}
      </div>

      <div>
        <h3 className="text-lg font-semibold text-cream">What's been built</h3>
        <ul className="mt-3 grid gap-2 sm:grid-cols-2">
          {MILESTONES.map((m) => (
            <li
              key={m}
              className="flex items-start gap-2 rounded border border-gold/10 bg-navy-soft/60 px-3 py-2 text-sm text-cream/80"
            >
              <span className="mt-0.5 text-gold">✓</span>
              <span>{m}</span>
            </li>
          ))}
        </ul>
      </div>

      <div className="rounded-lg border border-gold/20 bg-navy-soft p-5">
        <p className="text-sm text-cream/70">
          Full source, commit history, and the real GitHub issues/PRs behind each feature
          are public on{' '}
          <a
            href="https://github.com/nilushamadhuwanthi123/uninex-campus-hub"
            target="_blank"
            rel="noreferrer"
            className="text-gold underline underline-offset-2"
          >
            GitHub
          </a>
          .
        </p>
      </div>
    </div>
  )
}
