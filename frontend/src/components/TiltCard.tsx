import { useRef, useState, type CSSProperties, type ReactNode } from 'react'

interface TiltCardProps {
  children: ReactNode
  className?: string
  /** Maximum rotation in degrees on either axis. */
  maxTilt?: number
  /** Extra inline styles merged in, e.g. an animation-delay for staggered entrances. */
  style?: CSSProperties
}

const RESET_TRANSFORM = 'perspective(700px) rotateX(0deg) rotateY(0deg) scale3d(1, 1, 1)'

/**
 * Wraps its children in a card that tilts in 3D toward the cursor, with a
 * soft gold glare that follows the pointer. Pure CSS transforms + React
 * state -- no animation library. Disabled automatically for anyone with
 * prefers-reduced-motion set.
 */
export default function TiltCard({ children, className = '', maxTilt = 10, style }: TiltCardProps) {
  const ref = useRef<HTMLDivElement>(null)
  const [transform, setTransform] = useState(RESET_TRANSFORM)
  const [glareStyle, setGlareStyle] = useState<CSSProperties>({ opacity: 0 })

  const prefersReducedMotion =
    typeof window !== 'undefined' &&
    window.matchMedia?.('(prefers-reduced-motion: reduce)').matches

  function handleMouseMove(e: React.MouseEvent<HTMLDivElement>) {
    if (prefersReducedMotion || !ref.current) return
    const rect = ref.current.getBoundingClientRect()
    const px = (e.clientX - rect.left) / rect.width
    const py = (e.clientY - rect.top) / rect.height
    const rotateY = (px - 0.5) * 2 * maxTilt
    const rotateX = (0.5 - py) * 2 * maxTilt

    setTransform(
      `perspective(700px) rotateX(${rotateX}deg) rotateY(${rotateY}deg) scale3d(1.02, 1.02, 1.02)`,
    )
    setGlareStyle({
      opacity: 1,
      background: `radial-gradient(circle at ${px * 100}% ${py * 100}%, rgba(201, 168, 92, 0.16), transparent 60%)`,
    })
  }

  function handleMouseLeave() {
    setTransform(RESET_TRANSFORM)
    setGlareStyle({ opacity: 0 })
  }

  return (
    <div
      ref={ref}
      onMouseMove={handleMouseMove}
      onMouseLeave={handleMouseLeave}
      className={`relative [transform-style:preserve-3d] transition-transform duration-200 ease-out will-change-transform ${className}`}
      style={{ ...style, transform }}
    >
      {children}
      <div
        aria-hidden
        className="pointer-events-none absolute inset-0 rounded-lg transition-opacity duration-200"
        style={glareStyle}
      />
    </div>
  )
}
