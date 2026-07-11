import { Sparkles } from 'lucide-react'

export function EmptyState() {
  return (
    <div className="flex flex-col items-center justify-center py-24 px-6 text-center">
      {/* Icon container */}
      <div className="relative mb-6">
        <div className="w-20 h-20 rounded-2xl bg-secondary border border-border flex items-center justify-center">
          <svg
            viewBox="0 0 64 64"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
            className="w-10 h-10"
            aria-hidden="true"
          >
            {/* Play button shape */}
            <rect x="4" y="10" width="56" height="36" rx="6" className="fill-muted stroke-border" strokeWidth="1.5" />
            <path d="M26 22 L26 34 L38 28 Z" className="fill-muted-foreground/60" />
            {/* Segment lines below */}
            <rect x="4" y="52" width="18" height="3" rx="1.5" className="fill-primary/40" />
            <rect x="26" y="52" width="22" height="3" rx="1.5" className="fill-primary/60" />
            <rect x="52" y="52" width="8" height="3" rx="1.5" className="fill-primary/30" />
          </svg>
        </div>
        {/* Floating spark */}
        <div className="absolute -top-1.5 -right-1.5 w-6 h-6 rounded-full bg-primary/20 border border-primary/30 flex items-center justify-center">
          <Sparkles className="w-3 h-3 text-primary" />
        </div>
      </div>

      <h2 className="text-lg font-semibold text-foreground mb-2 text-balance">
        Paste a YouTube link to get started
      </h2>
      <p className="text-sm text-muted-foreground max-w-xs leading-relaxed text-balance">
        Our AI will analyze the video, identify key topics, and generate interactive chapters you can jump to instantly.
      </p>

      {/* Subtle grid of feature pills */}
      <div className="flex flex-wrap justify-center gap-2 mt-6">
        {['AI Analysis', 'Click-to-Seek', 'Topic Summaries', 'Time Segments'].map((feat) => (
          <span
            key={feat}
            className="text-xs px-2.5 py-1 rounded-full border border-border bg-secondary text-muted-foreground"
          >
            {feat}
          </span>
        ))}
      </div>
    </div>
  )
}
