import { Download, Expand, Minus, Minimize2, Plus, X } from 'lucide-react'
import { useEffect, useRef, useState } from 'react'
import type { SearchResult } from '../../types/location'

interface ImageryViewportProps {
  imageUrl: string
  result: SearchResult
}

export function ImageryViewport({ imageUrl, result }: ImageryViewportProps) {
  const [isExpanded, setIsExpanded] = useState(false)
  const [zoomLevel, setZoomLevel] = useState(0)
  const dialogRef = useRef<HTMLDialogElement>(null)

  useEffect(() => {
    const dialog = dialogRef.current
    if (!dialog) return
    if (isExpanded && !dialog.open) dialog.showModal()
    if (!isExpanded && dialog.open) dialog.close()
  }, [isExpanded])

  function downloadImage() {
    const link = document.createElement('a')
    link.href = imageUrl
    link.download = `orbitlens-${result.date}-${result.latitude.toFixed(4)}-${result.longitude.toFixed(4)}.png`
    link.click()
  }

  const alt = `NASA satellite observation of ${result.displayName} on ${formatLongDate(result.date)}`

  return (
    <div className="imagery-viewport">
      <div className="imagery-viewport__bar">
        <div><i aria-hidden="true" /><span>NASA GIBS capture</span><small>PNG / {result.resolution}²</small></div>
        <div className="viewport-actions">
          <div className="zoom-controls" aria-label="Image zoom controls">
            <button type="button" onClick={() => setZoomLevel((level) => Math.max(0, level - 1))} disabled={zoomLevel === 0} aria-label="Zoom out"><Minus size={15} /></button>
            <output aria-live="polite">{[100, 125, 150, 200][zoomLevel]}%</output>
            <button type="button" onClick={() => setZoomLevel((level) => Math.min(3, level + 1))} disabled={zoomLevel === 3} aria-label="Zoom in"><Plus size={15} /></button>
          </div>
          <button type="button" onClick={() => setIsExpanded(true)}><Expand size={16} /><span>Expand</span></button>
          <button type="button" onClick={downloadImage}><Download size={16} /><span>Download</span></button>
        </div>
      </div>
      <div className={`imagery-viewport__canvas imagery-viewport__canvas--zoom-${zoomLevel}`}>
        <img src={imageUrl} alt={alt} />
        <span className="viewport-coordinate viewport-coordinate--lat">LAT {result.latitude.toFixed(5)}°</span>
        <span className="viewport-coordinate viewport-coordinate--lon">LON {result.longitude.toFixed(5)}°</span>
        <span className="viewport-reticle" aria-hidden="true" />
        <span className="viewport-corner viewport-corner--tl" aria-hidden="true" />
        <span className="viewport-corner viewport-corner--tr" aria-hidden="true" />
        <span className="viewport-corner viewport-corner--bl" aria-hidden="true" />
        <span className="viewport-corner viewport-corner--br" aria-hidden="true" />
        <div className="capture-reveal" aria-hidden="true" />
      </div>

      <dialog ref={dialogRef} className="image-dialog" onClose={() => setIsExpanded(false)}>
        <div className="dialog-toolbar"><span>{result.displayName}</span><button type="button" onClick={() => setIsExpanded(false)}><X size={19} /><span>Close</span></button></div>
        <img src={imageUrl} alt={`Expanded ${alt}`} />
        <button className="dialog-minimize" type="button" onClick={() => setIsExpanded(false)}><Minimize2 size={16} /> Return to observation</button>
      </dialog>
    </div>
  )
}

function formatLongDate(date: string): string {
  return new Intl.DateTimeFormat('en-US', { dateStyle: 'long', timeZone: 'UTC' }).format(new Date(`${date}T00:00:00Z`))
}
