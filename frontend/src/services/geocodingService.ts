import type { LocationResponse } from '../types/location'
import { API_BASE_URL, ApiError, getFriendlyNetworkError } from './api'

export async function geocodeAddress(
  address: string,
  signal?: AbortSignal,
): Promise<LocationResponse> {
  const normalizedAddress = address.trim()

  if (!normalizedAddress) {
    throw new ApiError('Enter a location before starting the observation.', 400, 'validation')
  }
  if (normalizedAddress.length > 240) {
    throw new ApiError('Location is too long. Use at most 240 characters.', 400, 'validation')
  }
  const url = new URL(`${API_BASE_URL}/api/geocoding`, window.location.origin)
  url.searchParams.set('address', normalizedAddress)

  let response: Response
  try {
    response = await fetch(url, { signal, headers: { Accept: 'application/json' } })
  } catch (error: unknown) {
    if (error instanceof DOMException && error.name === 'AbortError') throw error
    throw getFriendlyNetworkError()
  }

  if (!response.ok) {
    if (response.status === 400) throw new ApiError('Enter a valid address to continue.', 400, 'validation')
    if (response.status === 404) throw new ApiError('We could not find that location. Try a city, region, or more specific address.', 404, 'not-found')
    if (response.status >= 500) throw new ApiError('The ground link is responding, but the location service is temporarily unavailable.', response.status, 'ground-link')
    throw new ApiError('The location search could not be completed.', response.status)
  }

  const data: unknown = await response.json()
  if (!isLocationResponse(data)) {
    throw new ApiError('The location service returned an unexpected response.', null, 'unexpected')
  }
  return data
}

function isLocationResponse(value: unknown): value is LocationResponse {
  if (typeof value !== 'object' || value === null) return false
  const candidate = value as Record<string, unknown>
  return (
    typeof candidate.latitude === 'number' &&
    Number.isFinite(candidate.latitude) &&
    candidate.latitude >= -90 &&
    candidate.latitude <= 90 &&
    typeof candidate.longitude === 'number' &&
    Number.isFinite(candidate.longitude) &&
    candidate.longitude >= -180 &&
    candidate.longitude <= 180 &&
    typeof candidate.displayName === 'string' &&
    candidate.displayName.length > 0 &&
    candidate.displayName.length <= 1000
  )
}
