export interface SatelliteSource {
  catalogNumber: number
  spacecraft: string
  mission: string
  instrument: string
  operator: string
  orbitType: string
  altitude: string
  period: string
  launched: string
  statusNote: string
  sourceLabel: string
  sourceUrl: string
}

export interface OrbitElements {
  epoch: string
  inclinationDegrees: number
  eccentricity: number
  meanMotionRevolutionsPerDay: number
  rightAscensionAscendingNodeDegrees: number
  argumentOfPericenterDegrees: number
  meanAnomalyDegrees: number
  meanMotionDotRevolutionsPerDaySquared: number
  meanMotionDoubleDotRevolutionsPerDayCubed: number
  bStar: number
  ephemerisType: number
  elementSetNumber: number
  revolutionAtEpoch: number
}

export interface SatelliteResponse {
  catalogNumber: number
  objectId: string
  name: string
  orbitElements: OrbitElements
  orbitalPeriodMinutes: number
  semiMajorAxisKm: number
  approximateAltitudeKm: number
}
