import { useEffect, useState } from 'react'

const OPENING_DURATION_MS = 2400

export function OpeningSequence() {
  const [isVisible, setIsVisible] = useState(true)

  useEffect(() => {
    const timer = window.setTimeout(() => setIsVisible(false), OPENING_DURATION_MS)
    return () => window.clearTimeout(timer)
  }, [])

  useEffect(() => {
    let hasPlayed = false

    const play = async () => {
      if (hasPlayed) return
      hasPlayed = await playOpeningSound()
      if (hasPlayed) {
        window.removeEventListener('pointerdown', play)
        window.removeEventListener('keydown', play)
      }
    }

    void play()
    window.addEventListener('pointerdown', play, { once: true })
    window.addEventListener('keydown', play, { once: true })

    return () => {
      window.removeEventListener('pointerdown', play)
      window.removeEventListener('keydown', play)
    }
  }, [])

  if (!isVisible) return null

  return (
    <div className="opening-sequence" aria-hidden="true">
      <div className="opening-sequence__stars" />
      <div className="opening-sequence__identity">
        <span className="opening-sequence__source">Imagery data source</span>
        <div className="opening-sequence__nasa">
          <span>NASA</span>
          <i />
        </div>
        <strong>GIBS</strong>
        <small>Global Imagery Browse Services</small>
      </div>
      <div className="opening-sequence__handoff"><span /> OrbitLens observation interface</div>
    </div>
  )
}

async function playOpeningSound(): Promise<boolean> {
  const AudioContextConstructor = window.AudioContext
  if (!AudioContextConstructor) return false

  const context = new AudioContextConstructor()
  try {
    if (context.state === 'suspended') await context.resume()
    if (context.state !== 'running') {
      void context.close()
      return false
    }

    const masterGain = context.createGain()
    const lowTone = context.createOscillator()
    const highTone = context.createOscillator()
    const now = context.currentTime

    masterGain.gain.setValueAtTime(0.0001, now)
    masterGain.gain.exponentialRampToValueAtTime(0.035, now + 0.08)
    masterGain.gain.exponentialRampToValueAtTime(0.0001, now + 0.82)

    lowTone.type = 'sine'
    lowTone.frequency.setValueAtTime(164.81, now)
    lowTone.frequency.exponentialRampToValueAtTime(220, now + 0.68)

    highTone.type = 'sine'
    highTone.frequency.setValueAtTime(329.63, now + 0.12)
    highTone.frequency.exponentialRampToValueAtTime(440, now + 0.68)

    lowTone.connect(masterGain)
    highTone.connect(masterGain)
    masterGain.connect(context.destination)
    lowTone.start(now)
    highTone.start(now + 0.12)
    lowTone.stop(now + 0.85)
    highTone.stop(now + 0.85)
    window.setTimeout(() => void context.close(), 1000)
    return true
  } catch {
    void context.close()
    return false
  }
}
