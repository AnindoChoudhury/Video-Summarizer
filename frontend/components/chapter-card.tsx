'use client'

import { PlayCircle } from 'lucide-react'

interface VideoSegment {
  id: number
  topic: string
  summary: string
  startTimeSeconds: number
  endTimeSeconds: number
}

interface ChapterCardProps {
  segment: VideoSegment
  index: number
  isActive: boolean
  onSeek: (seconds: number) => void
}

function formatTime(seconds: number): string {
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  const s = Math.floor(seconds % 60)
  if (h > 0) {
    return `${h}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
  }
  return `${m}:${String(s).padStart(2, '0')}`
}

export function ChapterCard({ segment, index, isActive, onSeek }: ChapterCardProps) {
  return (
    <button
      onClick={() => onSeek(segment.startTimeSeconds)}
      className={`w-full text-left rounded-xl border p-4 transition-all duration-200 group cursor-pointer
        ${isActive
          ? 'border-primary/60 bg-primary/10 shadow-[0_0_0_1px_oklch(0.65_0.18_250/0.3)]'
          : 'border-border bg-card hover:border-border/60 hover:bg-secondary'
        }`}
      aria-label={`Seek to chapter: ${segment.topic}`}
    >
      <div className="flex items-start gap-3">
        {/* Chapter number */}
        <div className={`flex-shrink-0 w-7 h-7 rounded-md flex items-center justify-center text-xs font-mono font-semibold mt-0.5 transition-colors duration-200
          ${isActive ? 'bg-primary text-primary-foreground' : 'bg-secondary text-muted-foreground group-hover:bg-muted'}`}>
          {String(index + 1).padStart(2, '0')}
        </div>

        {/* Content */}
        <div className="flex-1 min-w-0">
          <div className="flex items-center justify-between gap-2 mb-1.5">
            <h3 className={`text-sm font-semibold leading-snug text-pretty transition-colors duration-200
              ${isActive ? 'text-foreground' : 'text-foreground/80 group-hover:text-foreground'}`}>
              {segment.topic}
            </h3>
            <PlayCircle
              className={`flex-shrink-0 w-4 h-4 transition-all duration-200
                ${isActive ? 'text-primary opacity-100' : 'text-muted-foreground opacity-0 group-hover:opacity-60'}`}
            />
          </div>

          <p className="text-xs text-muted-foreground leading-relaxed line-clamp-2 mb-2">
            {segment.summary}
          </p>

          <div className="flex items-center gap-1.5">
            <span className={`text-[10px] font-mono px-1.5 py-0.5 rounded transition-colors duration-200
              ${isActive ? 'bg-primary/20 text-primary' : 'bg-secondary text-muted-foreground'}`}>
              {formatTime(segment.startTimeSeconds)}
            </span>
            <span className="text-[10px] text-muted-foreground/40">→</span>
            <span className={`text-[10px] font-mono px-1.5 py-0.5 rounded transition-colors duration-200
              ${isActive ? 'bg-primary/20 text-primary' : 'bg-secondary text-muted-foreground'}`}>
              {formatTime(segment.endTimeSeconds)}
            </span>
          </div>
        </div>
      </div>
    </button>
  )
}
