import type { OrbitElements, SatelliteResponse } from '../types/satellite'
import { API_BASE_URL, ApiError, getFriendlyNetworkError } from './api'

export async function fetchSatellite(
  catalogNumber: number,
  signal?: AbortSignal,
): Promise<SatelliteResponse> {
  const url = new URL(`${API_BASE_URL}/api/satellites/${catalogNumber}`, window.location.origin)
  let response: Response
  try {
    response = await fetch(url, { signal, headers: { Accept: 'application/json' } })
  } catch (error: unknown) {
    if (error instanceof DOMException && error.name === 'AbortError') throw error
    throw getFriendlyNetworkError()
  }

  if (!response.ok) {
    if (response.status === 404) throw new ApiError('No current orbital elements were found for this spacecraft.', 404)
    throw new ApiError('Current orbital elements are temporarily unavailable.', response.status, 'ground-link')
  }
  const data: unknown = await response.json()
  if (!isSatelliteResponse(data)) {
    throw new ApiError('The orbital service returned an unexpected response.')
  }
  return data
}

function isSatelliteResponse(value: unknown): value is SatelliteResponse {
  if (!isRecord(value) || !isRecord(value.orbitElements)) return false
  const elements = value.orbitElements
  return isFiniteNumber(value.catalogNumber) && typeof value.objectId === 'string' &&
    typeof value.name === 'string' && isFiniteNumber(value.orbitalPeriodMinutes) &&
    isFiniteNumber(value.semiMajorAxisKm) && isFiniteNumber(value.approximateAltitudeKm) &&
    isOrbitElements(elements)
}

function isOrbitElements(value: unknown): value is OrbitElements {
  if (!isRecord(value)) return false
  return typeof value.epoch === 'string' && [
    'inclinationDegrees', 'eccentricity', 'meanMotionRevolutionsPerDay',
    'rightAscensionAscendingNodeDegrees', 'argumentOfPericenterDegrees',
    'meanAnomalyDegrees', 'meanMotionDotRevolutionsPerDaySquared',
    'meanMotionDoubleDotRevolutionsPerDayCubed', 'bStar', 'ephemerisType',
    'elementSetNumber', 'revolutionAtEpoch',
  ].every((key) => isFiniteNumber(value[key]))
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null
}

function isFiniteNumber(value: unknown): value is number {
  return typeof value === 'number' && Number.isFinite(value)
}
