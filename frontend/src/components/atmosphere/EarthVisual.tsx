import type { MissionStage } from '../../types/imagery'
import type { SearchResult } from '../../types/location'
import { OrbitalPath } from './OrbitalPath'

interface EarthVisualProps {
  stage: MissionStage
  result: SearchResult | null
}

export function EarthVisual({ stage, result }: EarthVisualProps) {
  const isAcquiring = stage !== 'idle'
  const hasTarget = Boolean(result)
  const isScanning = stage === 'imagery' || stage === 'rendering'

  return (
    <div className={`earth-scene ${isAcquiring ? 'earth-scene--acquiring' : ''} ${hasTarget ? 'earth-scene--targeted' : ''} ${isScanning ? 'earth-scene--scanning' : ''}`}>
      <OrbitalPath active={isAcquiring || hasTarget} />
      <div className="earth-scene__telemetry earth-scene__telemetry--top" aria-hidden="true">
        <span>OBS / EARTH</span><span>ALT / ORBITAL VIEW</span>
      </div>
      <div className="earth-glow" aria-hidden="true" />
      <div className="earth-atmosphere" aria-hidden="true" />
      <div className="earth" role="img" aria-label={result ? `Earth view centered on ${result.displayName}` : 'Stylized orbital view of Earth'}>
        <div className="earth__surface">
          {result ? <><EarthSurfaceMap centralLongitude={result.longitude} /><EarthSurfaceMap centralLongitude={result.longitude} /></> : <><EarthSurfaceMap /><EarthSurfaceMap /></>}
        </div>
        <div className="earth__clouds" aria-hidden="true"><span /><span /></div>
        <div className="earth__night" aria-hidden="true" />
        <div className="earth__rim" aria-hidden="true" />
        {isScanning && <div className="earth__scan" aria-hidden="true" />}
        {result && <div className="earth__target-surface" role="img" aria-label={`Acquired location at latitude ${result.latitude}, longitude ${result.longitude}`}><TargetMap latitude={result.latitude} longitude={result.longitude} /><TargetMap latitude={result.latitude} longitude={result.longitude} /></div>}
      </div>
      {result && <div className="earth-target-data earth-target-data--coordinates"><span>LAT</span>{result.latitude.toFixed(6)}°<br /><span>LON</span>{result.longitude.toFixed(6)}°</div>}
      {result && <div className="earth-target-data earth-target-data--bbox"><span>OBSERVATION AREA</span>{result.bbox}</div>}
      <div className="earth-scene__caption">
        <span className="technical-label">{hasTarget ? 'TARGET ACQUIRED' : 'LIVE MISSION SURFACE'}</span>
        <strong>{result ? `${result.latitude.toFixed(4)}° / ${result.longitude.toFixed(4)}°` : 'Global observation ready'}</strong>
        <small>{result ? 'View centered on resolved coordinates' : 'Awaiting location coordinates'}</small>
      </div>
    </div>
  )
}

interface EarthSurfaceMapProps {
  centralLongitude?: number
}

function EarthSurfaceMap({ centralLongitude }: EarthSurfaceMapProps) {
  const isTargeted = centralLongitude !== undefined
  const targetMapX = isTargeted ? longitudeToMapX(centralLongitude) : 300
  const viewBoxX = targetMapX - 300

  return (
    <svg viewBox={`${viewBoxX} 0 600 600`} preserveAspectRatio="none" aria-hidden="true">
      {[-600, 0, 600].map((offset) => <MapGeometry key={offset} offset={offset} />)}
    </svg>
  )
}

function MapGeometry({ offset }: { offset: number }) {
  return (
    <g transform={`translate(${offset} 0)`}>
      <g className="earth__land">
        <path d="M74 168l42-55 55-21 53 18 33 52-12 48-38 22-9 49-38 10-39-39-50-18z" />
        <path d="M206 296l50 12 33 43-8 62-31 79-27 39-21-76-29-47 8-65z" />
        <path d="M330 104l78-20 75 31 67 70-9 48-66 16-28 45-60-15-43-52-46-22 21-53z" />
        <path d="M411 288l79-13 60 57-15 75-63 37-46-30-10-67-35-34z" />
        <path d="M494 454l51 19 18 49-38 26-55-23z" />
      </g>
      <g className="earth__grid">
        <path d="M0 104h600M0 202h600M0 300h600M0 398h600M0 496h600" />
        <path d="M0 0v600M100 0v600M200 0v600M300 0v600M400 0v600M500 0v600M600 0v600" />
      </g>
    </g>
  )
}

function TargetMap({ latitude, longitude }: { latitude: number; longitude: number }) {
  const targetX = longitudeToMapX(longitude)
  const targetY = latitudeToGlobeY(latitude)
  const viewBoxX = targetX - 300

  return (
    <svg viewBox={`${viewBoxX} 0 600 600`} preserveAspectRatio="none" aria-hidden="true">
      <circle className="target-marker__pulse" cx={targetX} cy={targetY} r="24" />
      <circle className="target-marker__core" cx={targetX} cy={targetY} r="7" />
      <path className="target-marker__crosshair" d={`M${targetX} ${targetY - 18}v9M${targetX} ${targetY + 9}v9M${targetX - 18} ${targetY}h9M${targetX + 9} ${targetY}h9`} />
    </svg>
  )
}

function longitudeToMapX(longitude: number): number {
  return ((longitude + 180) / 360) * 600
}

function latitudeToGlobeY(latitude: number): number {
  const projected = ((90 - latitude) / 180) * 600
  return Math.min(565, Math.max(35, projected))
}
