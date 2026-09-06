interface OrbitalPathProps {
  active: boolean
}

export function OrbitalPath({ active }: OrbitalPathProps) {
  return (
    <div className={`orbital-ring ${active ? 'orbital-ring--active' : ''}`} aria-hidden="true">
      <svg className="orbital-ring__layer orbital-ring__layer--back" viewBox="0 0 900 650">
        <ellipse className="orbit orbit--primary" pathLength="1" cx="450" cy="325" rx="410" ry="128" transform="rotate(-16 450 325)" />
      </svg>
      <svg className="orbital-ring__layer orbital-ring__layer--front" viewBox="0 0 900 650">
        <ellipse className="orbit orbit--primary" pathLength="1" cx="450" cy="325" rx="410" ry="128" transform="rotate(-16 450 325)" />
      </svg>
      <div className="orbital-trackers orbital-trackers--back"><OrbitTrackers /></div>
      <div className="orbital-trackers orbital-trackers--front"><OrbitTrackers /></div>
      <span className="orbital-ring__label">Orbital visualization</span>
    </div>
  )
}

function OrbitTrackers() {
  return (
    <>
      <span className="tracker-plane tracker-plane--primary"><i className="orbital-ring__tracker orbital-ring__tracker--one" /></span>
    </>
  )
}
