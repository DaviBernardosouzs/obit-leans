import { OrbitMark } from './OrbitMark'
import { BackendStatus } from './mission/BackendStatus'
import { MissionClock } from './mission/MissionClock'

interface HeaderProps {
  isConnected: boolean | null
}

export function Header({ isConnected }: HeaderProps) {
  return (
    <header className="site-header">
      <a className="brand" href="#main" aria-label="OrbitLens home">
        <OrbitMark />
        <span>
          <strong>OrbitLens</strong>
          <small>Orbital observation platform</small>
        </span>
      </a>
      <nav className="header-meta" aria-label="Project navigation">
        <a href="#mission">Mission</a>
        <a href="#about">About</a>
        <MissionClock />
        <BackendStatus isConnected={isConnected} />
      </nav>
    </header>
  )
}
