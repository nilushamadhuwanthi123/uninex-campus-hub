import { useState } from 'react'
import HomePage from './pages/HomePage'
import ResourcesPage from './pages/ResourcesPage'
import BookingsPage from './pages/BookingsPage'
import IncidentsPage from './pages/IncidentsPage'
import ReviewsPage from './pages/ReviewsPage'
import DashboardPage from './pages/DashboardPage'

const TABS = [
  { key: 'home', label: 'Home', Page: HomePage },
  { key: 'dashboard', label: 'Dashboard', Page: DashboardPage },
  { key: 'resources', label: 'Resources', Page: ResourcesPage },
  { key: 'bookings', label: 'Bookings', Page: BookingsPage },
  { key: 'incidents', label: 'Incidents', Page: IncidentsPage },
  { key: 'reviews', label: 'Reviews', Page: ReviewsPage },
] as const

type TabKey = (typeof TABS)[number]['key']

function App() {
  const [active, setActive] = useState<TabKey>('home')
  const ActivePage = TABS.find((t) => t.key === active)!.Page

  function navigate(tab: string) {
    if (TABS.some((t) => t.key === tab)) {
      setActive(tab as TabKey)
    }
  }

  return (
    <div className="min-h-screen">
      <header className="border-b border-gold/20 px-6 py-4">
        <p className="text-xs uppercase tracking-[0.3em] text-gold">Uninex</p>
        <h1 className="text-xl font-semibold text-cream">Campus Resource &amp; Booking Hub</h1>
      </header>

      <nav className="flex gap-1 overflow-x-auto border-b border-gold/10 px-6">
        {TABS.map((tab) => (
          <button
            key={tab.key}
            onClick={() => setActive(tab.key)}
            className={`whitespace-nowrap border-b-2 px-3 py-3 text-sm font-medium transition-colors ${
              active === tab.key
                ? 'border-gold text-gold'
                : 'border-transparent text-cream/60 hover:text-cream'
            }`}
          >
            {tab.label}
          </button>
        ))}
      </nav>

      <main className="mx-auto max-w-5xl px-6 py-8">
        <ActivePage onNavigate={navigate} />
      </main>
    </div>
  )
}

export default App
