import type { SatelliteSource } from '../types/satellite'

const SOURCES: Record<string, SatelliteSource> = {
  MODIS_Terra_CorrectedReflectance_TrueColor: {
    catalogNumber: 25994,
    spacecraft: 'Terra',
    mission: 'Earth Observing System · AM-1',
    instrument: 'MODIS',
    operator: 'NASA',
    orbitType: 'Near-polar, sun-synchronous',
    altitude: '705 km nominal',
    period: '99 minutes',
    launched: '18 Dec 1999',
    statusNote: 'Terra has been in orbital drift since 2020; nominal orbit values are reference data, not live telemetry.',
    sourceLabel: 'NASA Terra mission',
    sourceUrl: 'https://terra.nasa.gov/about',
  },
  MODIS_Aqua_CorrectedReflectance_TrueColor: {
    catalogNumber: 27424,
    spacecraft: 'Aqua',
    mission: 'Earth Observing System · PM-1',
    instrument: 'MODIS',
    operator: 'NASA',
    orbitType: 'Near-polar, sun-synchronous',
    altitude: '705 km nominal',
    period: '98.8 minutes',
    launched: '04 May 2002',
    statusNote: 'Aqua is in free drift; nominal orbit values are reference data, not live telemetry.',
    sourceLabel: 'NASA Aqua mission',
    sourceUrl: 'https://aqua.nasa.gov/content/about-aqua',
  },
  VIIRS_SNPP_CorrectedReflectance_TrueColor: {
    catalogNumber: 37849,
    spacecraft: 'Suomi NPP',
    mission: 'National Polar-orbiting Partnership',
    instrument: 'VIIRS',
    operator: 'NASA · NOAA',
    orbitType: 'Polar, sun-synchronous',
    altitude: '824 km reference',
    period: 'Approx. 101 minutes',
    launched: '28 Oct 2011',
    statusNote: 'Orbit values are published mission references, not a live spacecraft position.',
    sourceLabel: 'NOAA VIIRS mission',
    sourceUrl: 'https://ncc.nesdis.noaa.gov/VIIRS/',
  },
}

export function getSatelliteSource(layer: string): SatelliteSource | null {
  return SOURCES[layer] ?? null
}
