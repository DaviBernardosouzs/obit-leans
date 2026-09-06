import { useEffect } from 'react'

export function SpaceBackdrop() {
  useEffect(() => {
    if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) return

    let frame = 0
    const handlePointer = (event: PointerEvent) => {
      window.cancelAnimationFrame(frame)
      frame = window.requestAnimationFrame(() => {
        const x = (event.clientX / window.innerWidth - 0.5) * 2
        const y = (event.clientY / window.innerHeight - 0.5) * 2
        document.documentElement.style.setProperty('--parallax-x', x.toFixed(3))
        document.documentElement.style.setProperty('--parallax-y', y.toFixed(3))
      })
    }

    window.addEventListener('pointermove', handlePointer, { passive: true })
    return () => {
      window.cancelAnimationFrame(frame)
      window.removeEventListener('pointermove', handlePointer)
      document.documentElement.style.removeProperty('--parallax-x')
      document.documentElement.style.removeProperty('--parallax-y')
    }
  }, [])

  return (
    <div className="space-backdrop" aria-hidden="true">
      <div className="space-backdrop__stars space-backdrop__stars--near" />
      <div className="space-backdrop__stars space-backdrop__stars--far" />
      <div className="space-backdrop__stars space-backdrop__stars--deep" />
      <div className="space-backdrop__haze" />
      <div className="space-backdrop__grain" />
    </div>
  )
}
