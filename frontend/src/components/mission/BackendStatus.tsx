interface BackendStatusProps {
  isConnected: boolean | null
}

export function BackendStatus({ isConnected }: BackendStatusProps) {
  const state = isConnected === null ? 'standby' : isConnected ? 'online' : 'offline'
  const label = state === 'online' ? 'Ground link online' : state === 'offline' ? 'Ground link unavailable' : 'Ground link standby'

  return <span className={`backend-status backend-status--${state}`}><i aria-hidden="true" />{label}</span>
}
