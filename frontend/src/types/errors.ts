export type MissionErrorKind =
  | 'validation'
  | 'not-found'
  | 'ground-link'
  | 'imagery-source'
  | 'empty-capture'
  | 'unexpected'

export interface MissionError {
  kind: MissionErrorKind
  title: string
  message: string
  action: string
}
