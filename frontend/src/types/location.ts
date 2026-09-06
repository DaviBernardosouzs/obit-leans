export interface LocationResponse {
  latitude: number
  longitude: number
  displayName: string
}

export interface SearchResult extends LocationResponse {
  bbox: string
  date: string
  layer: string
  layerLabel: string
  resolution: number
}
