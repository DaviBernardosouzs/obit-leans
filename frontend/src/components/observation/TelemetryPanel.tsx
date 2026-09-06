import { Box, CalendarDays, Layers3, ScanLine } from 'lucide-react'
import type { SearchResult } from '../../types/location'
import { formatDate } from '../../utils/date'

interface TelemetryPanelProps {
  result: SearchResult
}

export function TelemetryPanel({ result }: TelemetryPanelProps) {
  return (
    <aside className="telemetry-panel" aria-label="Observation telemetry">
      <div className="telemetry-panel__header"><span className="technical-label">CAPTURE TELEMETRY</span><i aria-hidden="true" /></div>
      <dl>
        <div className="telemetry-location"><dt>Resolved location</dt><dd>{result.displayName}</dd></div>
        <div><dt>Latitude</dt><dd>{result.latitude.toFixed(7)}°</dd></div>
        <div><dt>Longitude</dt><dd>{result.longitude.toFixed(7)}°</dd></div>
        <div><dt><Box size={13} aria-hidden="true" /> Observation BBOX</dt><dd>{result.bbox}</dd></div>
        <div><dt><CalendarDays size={13} aria-hidden="true" /> Capture date</dt><dd>{formatDate(result.date)}</dd></div>
        <div><dt><Layers3 size={13} aria-hidden="true" /> Imagery layer</dt><dd>{result.layerLabel}</dd></div>
        <div><dt><ScanLine size={13} aria-hidden="true" /> Resolution</dt><dd>{result.resolution} × {result.resolution} px</dd></div>
      </dl>
      <div className="future-module"><span>Temporal comparison</span><small>Coming later</small></div>
    </aside>
  )
}
