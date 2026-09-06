import { useEffect, useState } from 'react'

export function MissionClock() {
  const [time, setTime] = useState(() => new Date())

  useEffect(() => {
    const interval = window.setInterval(() => setTime(new Date()), 1000)
    return () => window.clearInterval(interval)
  }, [])

  const formatted = new Intl.DateTimeFormat('en-GB', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false,
    timeZone: 'UTC',
  }).format(time)

  return <time className="mission-clock" dateTime={time.toISOString()}><span>UTC</span>{formatted}</time>
}
