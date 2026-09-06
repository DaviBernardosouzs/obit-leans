import { Code2, ExternalLink, Star } from 'lucide-react'

const REPOSITORY_URL = 'https://github.com/DaviBernardosouzs/obit-leans'

export function ProjectRepository() {
  return (
    <section className="project-repository" aria-labelledby="repository-title">
      <div className="project-repository__icon" aria-hidden="true"><Code2 /></div>
      <div className="project-repository__copy">
        <span className="technical-label">OPEN PROJECT / GITHUB</span>
        <h2 id="repository-title">Explore the OrbitLens source.</h2>
        <p>Support OrbitLens by opening the repository and selecting Star at the top of the GitHub page.</p>
      </div>
      <div className="project-repository__meta">
        <span><Code2 size={14} aria-hidden="true" /> DaviBernardosouzs / obit-leans</span>
        <a href={REPOSITORY_URL} target="_blank" rel="noopener noreferrer" referrerPolicy="no-referrer">
          <Star size={15} aria-hidden="true" /> Star on GitHub <ExternalLink size={13} aria-hidden="true" />
        </a>
      </div>
    </section>
  )
}
