import { ExternalLink, Orbit, Radio, Satellite } from 'lucide-react'
import type { SearchResult } from '../../types/location'
import type { OrbitElements, SatelliteResponse } from '../../types/satellite'
import { getSatelliteSource } from '../../utils/satelliteSource'

interface SourceSpacecraftProps {
  result: SearchResult
  satellite: SatelliteResponse | null
  satelliteError: string | null
}

export function SourceSpacecraft({ result, satellite, satelliteError }: SourceSpacecraftProps) {
  const source = getSatelliteSource(result.layer)
  if (!source) return null

  return (
    <section className="source-spacecraft" aria-labelledby="spacecraft-title">
      <div className="source-spacecraft__identity">
        <div className="spacecraft-visual" aria-hidden="true">
          <span className="spacecraft-visual__orbit" />
          <Satellite size={34} />
        </div>
        <div>
          <span className="technical-label">SELECTED SOURCE SPACECRAFT</span>
          <h3 id="spacecraft-title">{source.spacecraft}</h3>
          <p>{source.mission}</p>
          <span className="source-match"><Radio size={12} aria-hidden="true" /> Matched from layer · {result.layer}</span>
        </div>
      </div>

      <dl className="spacecraft-facts">
        <div><dt>Instrument</dt><dd>{source.instrument}</dd></div>
        <div><dt>Operator</dt><dd>{source.operator}</dd></div>
        <div><dt>Orbit</dt><dd>{source.orbitType}</dd></div>
        <div><dt>Reference altitude</dt><dd>{source.altitude}</dd></div>
        <div><dt>Orbital period</dt><dd>{source.period}</dd></div>
        <div><dt>Launch</dt><dd>{source.launched}</dd></div>
      </dl>

      <div className="spacecraft-disclaimer">
        <Orbit size={17} aria-hidden="true" />
        <p><strong>Mission reference, not live tracking.</strong> {source.statusNote}</p>
        <a href={source.sourceUrl} target="_blank" rel="noopener noreferrer" referrerPolicy="no-referrer">{source.sourceLabel}<ExternalLink size={13} aria-hidden="true" /></a>
      </div>
      <OrbitalElementsPanel satellite={satellite} error={satelliteError} />
    </section>
  )
}

function OrbitalElementsPanel({ satellite, error }: {
  satellite: SatelliteResponse | null
  error: string | null
}) {
  return (
    <section className="orbital-elements" aria-labelledby="orbital-elements-title">
      <div className="orbital-elements__heading">
        <div>
          <span className="technical-label">CELESTRAK GP / SGP4 INPUT</span>
          <h4 id="orbital-elements-title">Current orbital elements</h4>
        </div>
        {satellite && <span>NORAD {satellite.catalogNumber} · {satellite.objectId}</span>}
      </div>

      {satellite ? (
        <>
          <dl className="orbital-elements__grid">
            <Metric label="Object name" value={satellite.name} />
            <Metric label="Epoch UTC" value={formatEpoch(satellite.orbitElements.epoch)} />
            <Metric label="Inclination" value={degrees(satellite.orbitElements.inclinationDegrees)} />
            <Metric label="Eccentricity" value={precise(satellite.orbitElements.eccentricity)} />
            <Metric label="Mean motion" value={`${precise(satellite.orbitElements.meanMotionRevolutionsPerDay)} rev/day`} />
            <Metric label="RA ascending node" value={degrees(satellite.orbitElements.rightAscensionAscendingNodeDegrees)} />
            <Metric label="Argument of pericenter" value={degrees(satellite.orbitElements.argumentOfPericenterDegrees)} />
            <Metric label="Mean anomaly" value={degrees(satellite.orbitElements.meanAnomalyDegrees)} />
            <Metric label="Mean motion derivative" value={`${scientific(satellite.orbitElements.meanMotionDotRevolutionsPerDaySquared)} rev/day²`} />
            <Metric label="Mean motion 2nd derivative" value={`${scientific(satellite.orbitElements.meanMotionDoubleDotRevolutionsPerDayCubed)} rev/day³`} />
            <Metric label="B* drag term" value={scientific(satellite.orbitElements.bStar)} />
            <Metric label="Ephemeris type" value={String(satellite.orbitElements.ephemerisType)} />
            <Metric label="Element set" value={String(satellite.orbitElements.elementSetNumber)} />
            <Metric label="Revolution at epoch" value={String(satellite.orbitElements.revolutionAtEpoch)} />
            <Metric label="Orbital period" value={`${satellite.orbitalPeriodMinutes.toFixed(3)} min`} derived />
            <Metric label="Semi-major axis" value={`${satellite.semiMajorAxisKm.toFixed(3)} km`} derived />
            <Metric label="Mean approximate altitude" value={`${satellite.approximateAltitudeKm.toFixed(3)} km`} derived />
          </dl>
          <DataLimitations elements={satellite.orbitElements} />
        </>
      ) : (
        <div className="orbital-elements__unavailable" role="status">
          <strong>Orbital elements unavailable</strong>
          <span>{error ?? 'No NORAD catalog mapping is available for this imagery layer.'}</span>
        </div>
      )}
    </section>
  )
}

function Metric({ label, value, derived = false }: { label: string; value: string; derived?: boolean }) {
  return <div className={derived ? 'orbital-metric--derived' : undefined}><dt>{label}</dt><dd>{value}</dd></div>
}

function DataLimitations({ elements }: { elements: OrbitElements }) {
  return (
    <div className="orbital-elements__limitations">
      <span className="technical-label">NOT INCLUDED IN THIS ELEMENT SET</span>
      <p>Live onboard telemetry, optical visibility, image acquisition time, and instantaneous position are not fields of the CelesTrak GP element set. Position and passes must be propagated for a specific UTC instant and observer.</p>
      <small>Element epoch: {formatEpoch(elements.epoch)} · Values become less reliable as the propagation instant moves away from this epoch.</small>
    </div>
  )
}

function formatEpoch(epoch: string): string {
  const date = new Date(epoch)
  return Number.isNaN(date.getTime()) ? epoch : date.toISOString().replace('.000Z', 'Z')
}

function precise(value: number): string {
  return value.toLocaleString('en-US', { maximumFractionDigits: 10, useGrouping: false })
}

function scientific(value: number): string {
  return value.toExponential(6)
}

function degrees(value: number): string {
  return `${value.toFixed(6)}°`
}
