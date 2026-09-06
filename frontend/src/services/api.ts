const configuredBaseUrl = import.meta.env.VITE_API_BASE_URL?.trim()

// In development, Vite proxies /api to the configured backend. Production builds
// keep the explicit base URL and therefore require CORS for the deployed origin.
export const API_BASE_URL = import.meta.env.DEV
  ? ''
  : configuredBaseUrl?.replace(/\/$/, '') ?? ''

export class ApiError extends Error {
  readonly status: number | null
  readonly kind: import('../types/errors').MissionErrorKind

  constructor(
    message: string,
    status: number | null = null,
    kind: import('../types/errors').MissionErrorKind = 'unexpected',
  ) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.kind = kind
  }
}

export function getFriendlyNetworkError(): ApiError {
  return new ApiError('OrbitLens could not reach the ground link. Check that the backend is running on port 8081.', null, 'ground-link')
}
