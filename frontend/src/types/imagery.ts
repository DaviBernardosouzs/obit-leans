export interface ImageryRequest {
  address: string
  layer: string
  width: number
  height: number
  time: string
  transparent: boolean
}

export type MissionStage =
  | 'idle'
  | 'geocoding'
  | 'coordinates'
  | 'area'
  | 'imagery'
  | 'rendering'
