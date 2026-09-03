export default function Footer({ onNavigate }: { onNavigate?: (tab: string) => void }) {
  const year = new Date().getFullYear()
  return (
    <footer className="border-t border-gold/10 px-6 py-8 text-sm text-cream/60">
      <div className="mx-auto flex max-w-5xl flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <p>© {year} Uninex Campus Hub — built by Nilusha Madhuwanthi</p>
        <div className="flex flex-wrap gap-4">
          <button onClick={() => onNavigate?.('about')} className="hover:text-gold">
            About
          </button>
          <button onClick={() => onNavigate?.('contact')} className="hover:text-gold">
            Contact
          </button>
          <a
            href="https://github.com/nilushamadhuwanthi123/uninex-campus-hub"
            target="_blank"
            rel="noreferrer"
            className="hover:text-gold"
          >
            GitHub
          </a>
        </div>
      </div>
    </footer>
  )
}
