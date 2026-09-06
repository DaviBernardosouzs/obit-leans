const BBOX_OFFSET = 0.15

export function createBoundingBox(latitude: number, longitude: number): string {
  // Mirrors the backend radius for display only; NASA requests send the address
  // and the backend remains authoritative for clamping geographic coordinates.
  const minLongitude = Math.max(-180, longitude - BBOX_OFFSET)
  const minLatitude = Math.max(-90, latitude - BBOX_OFFSET)
  const maxLongitude = Math.min(180, longitude + BBOX_OFFSET)
  const maxLatitude = Math.min(90, latitude + BBOX_OFFSET)

  return [minLongitude, minLatitude, maxLongitude, maxLatitude]
    .map((coordinate) => coordinate.toFixed(7))
    .join(',')
}
