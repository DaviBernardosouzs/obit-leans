import { useEffect, useRef, useState } from 'react'
import { EarthVisual } from '../components/atmosphere/EarthVisual'
import { SpaceBackdrop } from '../components/atmosphere/SpaceBackdrop'
import { ErrorState } from '../components/feedback/ErrorState'
import { Header } from '../components/Header'
import { MissionComposer } from '../components/mission/MissionComposer'
import { MissionProgress } from '../components/mission/MissionProgress'
import { ObservationDeck } from '../components/observation/ObservationDeck'
import { OpeningSequence } from '../components/OpeningSequence'
import { ProjectRepository } from '../components/ProjectRepository'
import { ApiError } from '../services/api'
import { geocodeAddress } from '../services/geocodingService'
import { fetchImagery } from '../services/imageryService'
import { fetchSatellite } from '../services/satelliteService'
import type { MissionError, MissionErrorKind } from '../types/errors'
import type { MissionStage } from '../types/imagery'
import { NASA_LAYERS } from '../types/layers'
import type { SearchResult } from '../types/location'
import type { SearchValues } from '../types/search'
import type { SatelliteResponse } from '../types/satellite'
import { createBoundingBox } from '../utils/bbox'
import { getSatelliteSource } from '../utils/satelliteSource'

export function ExplorerPage() {
  const [missionStage, setMissionStage] = useState<MissionStage>('idle')
  const [result, setResult] = useState<SearchResult | null>(null)
  const [imageUrl, setImageUrl] = useState<string | null>(null)
  const [error, setError] = useState<MissionError | null>(null)
  const [isConnected, setIsConnected] = useState<boolean | null>(null)
  const [satellite, setSatellite] = useState<SatelliteResponse | null>(null)
  const [satelliteError, setSatelliteError] = useState<string | null>(null)
  const [focusRequest, setFocusRequest] = useState(0)
  const imageUrlRef = useRef<string | null>(null)
  const activeRequestRef = useRef<AbortController | null>(null)
  const lastSearchRef = useRef<SearchValues | null>(null)

  function replaceImageUrl(nextUrl: string | null) {
    if (imageUrlRef.current) URL.revokeObjectURL(imageUrlRef.current)
    imageUrlRef.current = nextUrl
    setImageUrl(nextUrl)
  }

  useEffect(() => () => {
    activeRequestRef.current?.abort()
    if (imageUrlRef.current) URL.revokeObjectURL(imageUrlRef.current)
  }, [])

  useEffect(() => {
    if (!imageUrl) return
    document.getElementById('observation')?.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }, [imageUrl])

  async function handleSearch(values: SearchValues) {
    activeRequestRef.current?.abort()
    const controller = new AbortController()
    activeRequestRef.current = controller
    lastSearchRef.current = values
    setError(null)
    setSatellite(null)
    setSatelliteError(null)
    replaceImageUrl(null)
    setMissionStage('geocoding')

    try {
      const location = await geocodeAddress(values.address, controller.signal)
      setIsConnected(true)
      setMissionStage('coordinates')

      const bbox = createBoundingBox(location.latitude, location.longitude)
      setMissionStage('area')
      const selectedLayer = NASA_LAYERS.find((item) => item.value === values.layer)
      const nextResult: SearchResult = {
        ...location,
        bbox,
        date: values.date,
        layer: values.layer,
        layerLabel: selectedLayer?.label ?? values.layer,
        resolution: values.resolution,
      }
      setResult(nextResult)
      setMissionStage('imagery')

      const source = getSatelliteSource(values.layer)
      const satelliteRequest = source
        ? fetchSatellite(source.catalogNumber, controller.signal)
            .then(setSatellite)
            .catch((satelliteFailure: unknown) => {
              if (satelliteFailure instanceof DOMException && satelliteFailure.name === 'AbortError') return
              setSatelliteError(satelliteFailure instanceof Error
                ? satelliteFailure.message
                : 'Current orbital elements are unavailable.')
            })
        : Promise.resolve()

      const blob = await fetchImagery({
        address: values.address,
        layer: values.layer,
        width: values.resolution,
        height: values.resolution,
        time: values.date,
        transparent: false,
      }, controller.signal)

      await satelliteRequest

      setMissionStage('rendering')
      replaceImageUrl(URL.createObjectURL(blob))
    } catch (caught: unknown) {
      if (caught instanceof DOMException && caught.name === 'AbortError') return
      replaceImageUrl(null)
      const missionError = toMissionError(caught)
      setError(missionError)
      if (missionError.kind === 'ground-link') setIsConnected(false)
    } finally {
      if (activeRequestRef.current === controller) {
        activeRequestRef.current = null
        setMissionStage('idle')
      }
    }
  }

  function returnToComposer() {
    document.getElementById('mission')?.scrollIntoView({ behavior: 'smooth', block: 'center' })
    setFocusRequest((value) => value + 1)
  }

  function handleErrorAction() {
    if (error?.kind === 'ground-link' && lastSearchRef.current) {
      void handleSearch(lastSearchRef.current)
      return
    }
    setError(null)
    returnToComposer()
  }

  const isLoading = missionStage !== 'idle'

  return (
    <div className={`app-shell ${imageUrl ? 'app-shell--observed' : ''}`}>
      <OpeningSequence />
      <SpaceBackdrop />
      <Header isConnected={isConnected} />
      <main id="main">
        <section className={`earth-overview ${isLoading ? 'earth-overview--mission-active' : ''} ${imageUrl ? 'earth-overview--mission-complete' : ''}`} id="mission" aria-labelledby="hero-title">
          <div className="earth-overview__visual"><EarthVisual stage={missionStage} result={result} /></div>
          <div className="earth-overview__content">
            <div className="hero-copy">
              <span className="technical-label">EARTH OBSERVATION / NASA GIBS</span>
              <h1 id="hero-title">Observe Earth from another perspective.</h1>
              <p>Turn any place into an orbital observation mission using real geocoding and satellite imagery.</p>
              <div className="hero-dataline" aria-hidden="true"><span>LAT ±90°</span><i /><span>LON ±180°</span><i /><span>WGS 84</span></div>
            </div>
            <MissionComposer isLoading={isLoading} focusRequest={focusRequest} onSubmit={handleSearch} />
          </div>
        </section>

        <MissionProgress stage={missionStage} isComplete={Boolean(imageUrl)} />
        {error && <ErrorState error={error} onAction={handleErrorAction} onDismiss={() => setError(null)} />}
        {imageUrl && result && <ObservationDeck imageUrl={imageUrl} result={result} satellite={satellite} satelliteError={satelliteError} onNewObservation={returnToComposer} />}
        <ProjectRepository />
      </main>
      <footer id="about">
        <div><strong>OrbitLens</strong><span>Open Earth observation interface</span></div>
        <p>Location data by Nominatim · Satellite imagery by NASA GIBS</p>
        <span className="footer-status">Built for future orbital modules</span>
      </footer>
    </div>
  )
}

function toMissionError(caught: unknown): MissionError {
  const kind: MissionErrorKind = caught instanceof ApiError ? caught.kind : 'unexpected'
  const message = caught instanceof Error ? caught.message : 'The observation was interrupted before completion.'
  const content: Record<MissionErrorKind, Pick<MissionError, 'title' | 'action'>> = {
    validation: { title: 'Location required', action: 'Review location' },
    'not-found': { title: 'Location not found', action: 'Edit location' },
    'ground-link': { title: 'Ground link unavailable', action: 'Try again' },
    'imagery-source': { title: 'Imagery source unavailable', action: 'Review selection' },
    'empty-capture': { title: 'No capture for this selection', action: 'Choose another date' },
    unexpected: { title: 'Observation interrupted', action: 'Review mission' },
  }
  return { kind, message, ...content[kind] }
}
