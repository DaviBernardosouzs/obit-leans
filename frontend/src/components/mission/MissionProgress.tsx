import { Check, Circle } from 'lucide-react'
import type { MissionStage } from '../../types/imagery'

const STAGES = [
  { id: 'geocoding', number: '01', label: 'Geocoding', detail: 'Nominatim' },
  { id: 'coordinates', number: '02', label: 'Target lock', detail: 'Coordinates' },
  { id: 'area', number: '03', label: 'BBOX definition', detail: 'Observation area' },
  { id: 'imagery', number: '04', label: 'GIBS request', detail: 'NASA imagery' },
  { id: 'rendering', number: '05', label: 'Capture ready', detail: 'Blob rendered' },
] as const

interface MissionProgressProps {
  stage: MissionStage
  isComplete: boolean
}

export function MissionProgress({ stage, isComplete }: MissionProgressProps) {
  const activeIndex = STAGES.findIndex((item) => item.id === stage)

  return (
    <section className={`mission-progress ${stage !== 'idle' ? 'mission-progress--active' : ''}`} aria-live="polite" aria-busy={stage !== 'idle'} aria-label="Mission timeline">
      <div className="mission-progress__header">
        <span className="technical-label">MISSION TIMELINE</span>
        <span className="signal-indicator"><i aria-hidden="true" /> {isComplete ? 'Capture ready' : stage !== 'idle' ? 'Mission active' : 'Awaiting target'}</span>
      </div>
      <ol>
        {STAGES.map((item, index) => {
          const state = isComplete || index < activeIndex ? 'complete' : index === activeIndex ? 'active' : 'waiting'
          return (
            <li key={item.id} className={`progress-step progress-step--${state}`}>
              <span className="progress-step__icon">{state === 'complete' ? <Check size={14} /> : <Circle size={state === 'active' ? 8 : 6} />}</span>
              <span><em>{item.number}</em><strong>{item.label}</strong><small>{item.detail}</small></span>
            </li>
          )
        })}
      </ol>
    </section>
  )
}
