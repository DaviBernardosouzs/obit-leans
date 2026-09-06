import { RotateCcw } from 'lucide-react'
import type { SearchResult } from '../../types/location'
import { ImageryViewport } from './ImageryViewport'
import { SourceSpacecraft } from './SourceSpacecraft'
import { TelemetryPanel } from './TelemetryPanel'
import type { SatelliteResponse } from '../../types/satellite'

interface ObservationDeckProps {
  imageUrl: string
  result: SearchResult
  satellite: SatelliteResponse | null
  satelliteError: string | null
  onNewObservation: () => void
}

export function ObservationDeck({ imageUrl, result, satellite, satelliteError, onNewObservation }: ObservationDeckProps) {
  return (
    <section className="observation-deck" id="observation" aria-labelledby="observation-title">
      <div className="observation-deck__heading">
        <div><span className="technical-label">OBSERVATION DECK / CAPTURE READY</span><h2 id="observation-title">Earth, acquired.</h2></div>
        <button className="secondary-button" type="button" onClick={onNewObservation}><RotateCcw size={16} /> Return to Earth</button>
      </div>
      <div className="observation-deck__layout">
        <ImageryViewport imageUrl={imageUrl} result={result} />
        <TelemetryPanel result={result} />
      </div>
      <SourceSpacecraft result={result} satellite={satellite} satelliteError={satelliteError} />
    </section>
  )
}
