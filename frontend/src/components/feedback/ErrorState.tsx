import { AlertTriangle, CloudOff, ImageOff, MapPinOff, RadioTower } from 'lucide-react'
import type { MissionError } from '../../types/errors'

interface ErrorStateProps {
  error: MissionError
  onAction: () => void
  onDismiss: () => void
}

export function ErrorState({ error, onAction, onDismiss }: ErrorStateProps) {
  const Icon = error.kind === 'not-found' ? MapPinOff : error.kind === 'ground-link' ? RadioTower : error.kind === 'imagery-source' ? CloudOff : error.kind === 'empty-capture' ? ImageOff : AlertTriangle
  return (
    <section className={`mission-error mission-error--${error.kind}`} role="alert">
      <div className="mission-error__icon"><Icon aria-hidden="true" /></div>
      <div className="mission-error__copy">
        <span className="technical-label">MISSION INTERRUPTED</span>
        <h2>{error.title}</h2>
        <p>{error.message}</p>
      </div>
      <div className="mission-error__actions">
        <button type="button" className="secondary-button" onClick={onAction}>{error.action}</button>
        <button type="button" className="quiet-button" onClick={onDismiss}>Dismiss</button>
      </div>
    </section>
  )
}
