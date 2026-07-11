'use client'

import { useState, useRef, useCallback } from 'react'
import dynamic from 'next/dynamic'
import { Sparkles, Link2, ChevronRight, LayoutList } from 'lucide-react'
import { ChapterCard } from './chapter-card'
import { EmptyState } from './empty-state'
import { LoadingState } from './loading-state'

// Dynamically import ReactPlayer to avoid SSR issues
const ReactPlayer = dynamic(() => import('react-player'), { ssr: false })

interface VideoSegment {
  id: number
  topic: string
  summary: string
  startTimeSeconds: number
  endTimeSeconds: number
}

interface VideoData {
  videoID: string
  status: string
  videoSegments: VideoSegment[]
}

type AppState = 'idle' | 'loading' | 'loaded'

const MOCK_DATA: VideoData = {
  videoID: 'aBxjDBC4M1U',
  status: 'COMPLETED',
  videoSegments: [
    {
      id: 12,
      topic: 'Introduction and Brute Force vs Disjoint Set',
      summary:
        'Learn why the disjoint set data structure is used, how it optimizes component connectivity queries to constant time compared to the linear time complexity of DFS/BFS, and its importance in dynamic graphs.',
      startTimeSeconds: 3.0,
      endTimeSeconds: 192.0,
    },
    {
      id: 13,
      topic: 'Union by Rank: Initialization and Pseudocode',
      summary:
        'Explore how to implement Union by Rank using Rank and Parent arrays, including the step-by-step pseudocode for combining components.',
      startTimeSeconds: 192.0,
      endTimeSeconds: 481.0,
    },
    {
      id: 14,
      topic: 'Dry Run of Union by Rank',
      summary:
        'Walk through a detailed dry run of Union by Rank with an example graph, observing how ranks and parent pointers are updated.',
      startTimeSeconds: 481.0,
      endTimeSeconds: 790.0,
    },
  ],
}

export function VideoSegmenter() {
  const [url, setUrl] = useState('')
  const [appState, setAppState] = useState<AppState>('idle')
  const [videoData, setVideoData] = useState<VideoData | null>(null)
  const [playedSeconds, setPlayedSeconds] = useState(0)
  const [urlError, setUrlError] = useState('')
  const playerRef = useRef<{ seekTo: (seconds: number, type?: string) => void } | null>(null)

  const handleGenerate = useCallback(() => {
    if (!url.trim()) {
      setUrlError('Please enter a YouTube URL.')
      return
    }
    setUrlError('')
    setAppState('loading')
    // Simulate 3-second AI processing
    setTimeout(() => {
      setVideoData(MOCK_DATA)
      setAppState('loaded')
    }, 3000)
  }, [url])

  const handleSeek = useCallback((seconds: number) => {
    if (playerRef.current) {
      playerRef.current.seekTo(seconds, 'seconds')
    }
  }, [])

  const handleProgress = useCallback(({ playedSeconds }: { playedSeconds: number }) => {
    setPlayedSeconds(playedSeconds)
  }, [])

  const getActiveSegmentIndex = useCallback(() => {
    if (!videoData) return -1
    return videoData.videoSegments.findIndex(
      (seg) => playedSeconds >= seg.startTimeSeconds && playedSeconds < seg.endTimeSeconds,
    )
  }, [videoData, playedSeconds])

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter' && !e.nativeEvent.isComposing) {
      handleGenerate()
    }
  }

  const activeIndex = getActiveSegmentIndex()
  const videoUrl = videoData ? `https://www.youtube.com/watch?v=${videoData.videoID}` : ''

  return (
    <div className="min-h-screen bg-background flex flex-col">
      {/* Header */}
      <header className="border-b border-border bg-card/60 backdrop-blur-sm sticky top-0 z-10">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 h-14 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <div className="w-7 h-7 rounded-lg bg-primary/20 border border-primary/30 flex items-center justify-center">
              <Sparkles className="w-3.5 h-3.5 text-primary" />
            </div>
            <span className="text-sm font-semibold text-foreground tracking-tight">Segmenter</span>
            <span className="hidden sm:inline-flex items-center gap-1 text-[10px] font-mono px-1.5 py-0.5 rounded-full bg-primary/15 text-primary border border-primary/20 ml-1">
              AI
            </span>
          </div>
          <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
            <LayoutList className="w-3.5 h-3.5" />
            <span className="hidden sm:inline">YouTube Chapter Generator</span>
          </div>
        </div>
      </header>

      {/* Hero / Input Section */}
      <section className="border-b border-border bg-card/30">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 py-10 sm:py-14">
          <div className="max-w-2xl">
            <div className="flex items-center gap-2 mb-4">
              <span className="text-[11px] font-mono tracking-widest text-primary uppercase">AI-Powered</span>
              <ChevronRight className="w-3.5 h-3.5 text-muted-foreground/40" />
              <span className="text-[11px] font-mono tracking-widest text-muted-foreground uppercase">Video Analysis</span>
            </div>
            <h1 className="text-3xl sm:text-4xl font-bold text-foreground leading-tight text-balance mb-3">
              Turn any YouTube video into interactive chapters
            </h1>
            <p className="text-sm text-muted-foreground leading-relaxed mb-8 text-pretty">
              Paste a YouTube URL and our AI will segment the video by topic — complete with summaries and click-to-seek navigation.
            </p>

            {/* URL Input */}
            <div className="flex flex-col sm:flex-row gap-2">
              <div className="flex-1 relative">
                <Link2 className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground pointer-events-none" />
                <input
                  type="url"
                  value={url}
                  onChange={(e) => { setUrl(e.target.value); setUrlError('') }}
                  onKeyDown={handleKeyDown}
                  placeholder="https://youtube.com/watch?v=..."
                  aria-label="YouTube URL"
                  className={`w-full h-11 pl-10 pr-4 rounded-xl border bg-secondary text-sm text-foreground placeholder:text-muted-foreground/50
                    focus:outline-none focus:ring-2 focus:ring-ring transition-all duration-150
                    ${urlError ? 'border-destructive focus:ring-destructive/40' : 'border-border focus:border-primary/40'}`}
                />
              </div>
              <button
                onClick={handleGenerate}
                disabled={appState === 'loading'}
                className="h-11 px-5 rounded-xl bg-primary text-primary-foreground text-sm font-semibold
                  hover:bg-primary/90 active:scale-[0.98] transition-all duration-150
                  disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2 whitespace-nowrap"
              >
                <Sparkles className="w-3.5 h-3.5" />
                Generate Chapters
              </button>
            </div>
            {urlError && (
              <p className="mt-2 text-xs text-destructive">{urlError}</p>
            )}
          </div>
        </div>
      </section>

      {/* Main Content */}
      <main className="flex-1 max-w-7xl mx-auto w-full px-4 sm:px-6 py-8">
        {appState === 'idle' && <EmptyState />}
        {appState === 'loading' && <LoadingState />}

        {appState === 'loaded' && videoData && (
          <div className="grid grid-cols-1 lg:grid-cols-[1fr_360px] gap-6 items-start">
            {/* Left: Video Player */}
            <div className="space-y-3">
              {/* Player wrapper */}
              <div className="rounded-xl overflow-hidden border border-border bg-black aspect-video w-full shadow-lg">
                <ReactPlayer
                  ref={playerRef as React.Ref<unknown>}
                  url={videoUrl}
                  width="100%"
                  height="100%"
                  controls
                  onProgress={handleProgress}
                  progressInterval={500}
                />
              </div>

              {/* Video info */}
              <div className="flex items-center gap-2 px-1">
                <span className="inline-flex items-center gap-1.5 text-[11px] font-mono px-2 py-0.5 rounded-full bg-primary/15 text-primary border border-primary/20">
                  <span className="w-1.5 h-1.5 rounded-full bg-primary animate-pulse" />
                  {videoData.status}
                </span>
                <span className="text-xs text-muted-foreground">
                  {videoData.videoSegments.length} chapters generated
                </span>
              </div>
            </div>

            {/* Right: Chapter Sidebar */}
            <aside aria-label="Video chapters" className="lg:sticky lg:top-[4.5rem]">
              <div className="flex items-center justify-between mb-3 px-1">
                <h2 className="text-xs font-semibold text-muted-foreground uppercase tracking-widest">Chapters</h2>
                <span className="text-xs text-muted-foreground font-mono">
                  {activeIndex >= 0 ? `${activeIndex + 1} / ${videoData.videoSegments.length}` : `— / ${videoData.videoSegments.length}`}
                </span>
              </div>

              <div className="space-y-2 max-h-[calc(100vh-12rem)] overflow-y-auto pr-1 scrollbar-thin">
                {videoData.videoSegments.map((segment, index) => (
                  <ChapterCard
                    key={segment.id}
                    segment={segment}
                    index={index}
                    isActive={activeIndex === index}
                    onSeek={handleSeek}
                  />
                ))}
              </div>
            </aside>
          </div>
        )}
      </main>
    </div>
  )
}
