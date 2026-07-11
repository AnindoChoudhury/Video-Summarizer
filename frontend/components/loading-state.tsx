import { Loader2, Sparkles } from 'lucide-react'

export function LoadingState() {
  return (
    <div className="flex flex-col items-center justify-center py-24 px-6 text-center">
      <div className="relative mb-6">
        <div className="w-16 h-16 rounded-2xl bg-secondary border border-border flex items-center justify-center">
          <Loader2 className="w-7 h-7 text-primary animate-spin" />
        </div>
        <div className="absolute -top-1.5 -right-1.5 w-6 h-6 rounded-full bg-primary/20 border border-primary/30 flex items-center justify-center">
          <Sparkles className="w-3 h-3 text-primary" />
        </div>
      </div>

      <h2 className="text-lg font-semibold text-foreground mb-2">AI is processing the video…</h2>
      <p className="text-sm text-muted-foreground max-w-xs leading-relaxed">
        Analyzing content, identifying topics, and generating chapter segments. This may take a moment.
      </p>

      {/* Skeleton cards */}
      <div className="w-full max-w-sm mt-8 space-y-3">
        {[1, 2, 3].map((i) => (
          <div
            key={i}
            className="rounded-xl border border-border bg-card p-4 animate-pulse"
            style={{ opacity: 1 - (i - 1) * 0.2 }}
          >
            <div className="flex items-start gap-3">
              <div className="w-7 h-7 rounded-md bg-secondary flex-shrink-0" />
              <div className="flex-1 space-y-2">
                <div className="h-3.5 bg-secondary rounded-md w-3/4" />
                <div className="h-3 bg-secondary rounded-md w-full" />
                <div className="h-3 bg-secondary rounded-md w-5/6" />
                <div className="flex gap-2 pt-1">
                  <div className="h-4 w-12 bg-secondary rounded-md" />
                  <div className="h-4 w-12 bg-secondary rounded-md" />
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
