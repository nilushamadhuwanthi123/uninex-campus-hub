const TONES: Record<string, string> = {
  REQUESTED: 'bg-bronze-mid/30 text-cream',
  APPROVED: 'bg-gold/20 text-gold',
  RESOLVED: 'bg-gold/20 text-gold',
  CLOSED: 'bg-gold/20 text-gold',
  REJECTED: 'bg-red-900/40 text-red-300',
  CANCELLED: 'bg-red-900/40 text-red-300',
  OPEN: 'bg-bronze-mid/30 text-cream',
  ASSIGNED: 'bg-bronze-mid/40 text-cream',
  IN_PROGRESS: 'bg-bronze-mid/40 text-cream',
}

export default function StatusBadge({ status }: { status: string }) {
  const tone = TONES[status] ?? 'bg-bronze-mid/30 text-cream'
  return (
    <span className={`px-2 py-0.5 rounded-full text-xs font-medium tracking-wide ${tone}`}>
      {status.replace('_', ' ')}
    </span>
  )
}
