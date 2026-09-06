import { CalendarDays, ChevronDown, MapPin, Radio, SlidersHorizontal } from 'lucide-react'
import { useEffect, useRef, useState, type FormEvent } from 'react'
import { NASA_LAYERS } from '../../types/layers'
import type { SearchValues } from '../../types/search'
import { getDefaultImageryDate, getToday } from '../../utils/date'

interface MissionComposerProps {
  isLoading: boolean
  focusRequest: number
  onSubmit: (values: SearchValues) => void
}

export function MissionComposer({ isLoading, focusRequest, onSubmit }: MissionComposerProps) {
  const [address, setAddress] = useState('')
  const [date, setDate] = useState(getDefaultImageryDate)
  const [layer, setLayer] = useState<string>(NASA_LAYERS[0].value)
  const [resolution, setResolution] = useState(512)
  const [validationError, setValidationError] = useState<string | null>(null)
  const addressRef = useRef<HTMLInputElement>(null)

  useEffect(() => {
    if (focusRequest > 0) addressRef.current?.focus()
  }, [focusRequest])

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const cleanAddress = address.trim()
    if (!cleanAddress) {
      setValidationError('Location required — enter an address or place name.')
      addressRef.current?.focus()
      return
    }
    if (cleanAddress.length > 240) {
      setValidationError('Location is too long — use at most 240 characters.')
      addressRef.current?.focus()
      return
    }
    if (!date) {
      setValidationError('Select an observation date before starting.')
      return
    }
    if (date > getToday()) {
      setValidationError('Observation dates cannot be in the future.')
      return
    }
    setValidationError(null)
    onSubmit({ address: cleanAddress, date, layer, resolution })
  }

  return (
    <form className={`mission-composer ${isLoading ? 'mission-composer--engaged' : ''}`} onSubmit={handleSubmit} noValidate>
      <div className="mission-composer__heading">
        <div><span className="technical-label">MISSION COMPOSER</span><h2>Set observation target</h2></div>
        <span className="mission-id">OL / 001</span>
      </div>

      <div className="mission-composer__controls">
      <label className={`mission-field mission-field--address ${validationError ? 'mission-field--error' : ''}`}>
        <span>Location</span>
        <div className="mission-control">
          <MapPin size={18} aria-hidden="true" />
          <input
            ref={addressRef}
            type="search"
            value={address}
            onChange={(event) => { setAddress(event.target.value); if (validationError) setValidationError(null) }}
            placeholder="City, region, or address"
            maxLength={240}
            autoComplete="street-address"
            aria-invalid={Boolean(validationError)}
            aria-describedby={validationError ? 'mission-validation' : undefined}
            disabled={isLoading}
          />
        </div>
        {validationError && <small id="mission-validation" role="alert">{validationError}</small>}
      </label>

      <div className="mission-composer__row">
        <label className="mission-field">
          <span>Observation date</span>
          <div className="mission-control">
            <CalendarDays size={17} aria-hidden="true" />
            <input type="date" value={date} max={getToday()} onChange={(event) => setDate(event.target.value)} disabled={isLoading} />
          </div>
        </label>
        <fieldset className="mission-field resolution-field">
          <legend>Resolution</legend>
          <div className="resolution-options">
            {[512, 1024].map((size) => (
              <label key={size}>
                <input type="radio" name="resolution" checked={resolution === size} onChange={() => setResolution(size)} disabled={isLoading} />
                <span>{size} × {size}</span>
              </label>
            ))}
          </div>
        </fieldset>
      </div>

      <details className="advanced-settings">
        <summary><span><SlidersHorizontal size={16} aria-hidden="true" /> Data source</span><small>{NASA_LAYERS.find((item) => item.value === layer)?.label}</small></summary>
        <label className="mission-field">
          <span>NASA imagery layer</span>
          <div className="mission-control mission-control--select">
            <select value={layer} onChange={(event) => setLayer(event.target.value)} disabled={isLoading}>
              {NASA_LAYERS.map((item) => <option key={item.value} value={item.value}>{item.label}</option>)}
            </select>
            <ChevronDown size={16} aria-hidden="true" />
          </div>
        </label>
      </details>
      </div>

      <button className="start-observation" type="submit" disabled={isLoading}>
        <Radio size={18} aria-hidden="true" />
        <span>{isLoading ? 'Mission sequence active' : 'Start observation'}</span>
        <i aria-hidden="true">→</i>
      </button>
      <p className="mission-composer__note">Location by Nominatim · Imagery by NASA GIBS</p>
    </form>
  )
}
