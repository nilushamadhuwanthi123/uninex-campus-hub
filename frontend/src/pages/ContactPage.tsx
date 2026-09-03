const LINKS = [
  {
    label: 'GitHub',
    value: 'nilushamadhuwanthi123',
    href: 'https://github.com/nilushamadhuwanthi123',
    hint: 'Source code, commit history and this project’s real issues & PRs',
  },
  {
    label: 'Email',
    value: 'nilushamadhuwanthi02@gmail.com',
    href: 'mailto:nilushamadhuwanthi02@gmail.com',
    hint: 'For questions about this project, feedback, or opportunities',
  },
]

export default function ContactPage() {
  return (
    <div className="space-y-8">
      <div>
        <p className="text-xs uppercase tracking-[0.3em] text-gold">Contact</p>
        <h2 className="mt-2 text-2xl font-semibold text-cream sm:text-3xl">Get in touch</h2>
        <p className="mt-3 max-w-xl text-sm leading-relaxed text-cream/70">
          This is a real solo project, not a company — reach out directly through either
          of these if you have a question, feedback, or want to talk about the build.
        </p>
      </div>

      <div className="grid gap-4 sm:grid-cols-2">
        {LINKS.map((l) => (
          <a
            key={l.label}
            href={l.href}
            target={l.label === 'GitHub' ? '_blank' : undefined}
            rel={l.label === 'GitHub' ? 'noreferrer' : undefined}
            className="block rounded-lg border border-gold/20 bg-navy-soft p-5 transition-colors hover:border-gold/50"
          >
            <h3 className="font-semibold text-gold">{l.label}</h3>
            <p className="mt-1 text-sm text-cream">{l.value}</p>
            <p className="mt-1 text-xs text-cream/60">{l.hint}</p>
          </a>
        ))}
      </div>
    </div>
  )
}
