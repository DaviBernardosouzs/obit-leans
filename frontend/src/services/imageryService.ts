import type { ImageryRequest } from '../types/imagery'
import { API_BASE_URL, ApiError, getFriendlyNetworkError } from './api'

const MAX_IMAGE_BYTES = 20 * 1024 * 1024

export async function fetchImagery(
  request: ImageryRequest,
  signal?: AbortSignal,
): Promise<Blob> {
  const url = new URL(`${API_BASE_URL}/api/imagery`, window.location.origin)
  Object.entries(request).forEach(([key, value]) => url.searchParams.set(key, String(value)))

  let response: Response
  try {
    response = await fetch(url, { signal, headers: { Accept: 'image/png' } })
  } catch (error: unknown) {
    if (error instanceof DOMException && error.name === 'AbortError') throw error
    throw getFriendlyNetworkError()
  }

  if (!response.ok) {
    if (response.status >= 500) throw new ApiError('NASA GIBS is temporarily unavailable. Try another observation date or try again later.', response.status, 'imagery-source')
    throw new ApiError('The satellite image could not be loaded for these parameters.', response.status, 'imagery-source')
  }

  const declaredSize = Number(response.headers.get('content-length'))
  if (Number.isFinite(declaredSize) && declaredSize > MAX_IMAGE_BYTES) {
    throw new ApiError('The imagery source returned a file larger than the safe display limit.', null, 'imagery-source')
  }

  const blob = await response.blob()
  if (blob.size === 0) throw new ApiError('No satellite image was returned for this location and date.', null, 'empty-capture')
  if (blob.size > MAX_IMAGE_BYTES) throw new ApiError('The imagery source returned a file larger than the safe display limit.', null, 'imagery-source')
  if (!blob.type.includes('image/png')) throw new ApiError('The imagery source returned an unsupported file format.', null, 'imagery-source')
  return blob
}
