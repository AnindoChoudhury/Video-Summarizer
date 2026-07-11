import json
import sys

from youtube_transcript_api import YouTubeTranscriptApi
from youtube_transcript_api._errors import (
    TranscriptsDisabled,
    NoTranscriptFound,
    VideoUnavailable
)


def get_transcript(video_id):
    """
    Fetch transcript using the YouTube video ID directly.

    Priority:
    1. English transcript, if available.
    2. Otherwise, the first available transcript.
    """

    try:
        ytt_api = YouTubeTranscriptApi()

        # Get all available transcripts
        transcript_list = ytt_api.list(video_id)

        selected_transcript = None

        # First try to find an English transcript
        try:
            selected_transcript = transcript_list.find_transcript(["en"])

        except NoTranscriptFound:
            # If English is unavailable, select the first available transcript
            for transcript in transcript_list:
                selected_transcript = transcript
                break

        if selected_transcript is None:
            return {
                "success": False,
                "video_id": video_id,
                "error": "No transcript available for this video."
            }

        # Fetch the selected transcript
        fetched_transcript = selected_transcript.fetch()

        segments = []

        for snippet in fetched_transcript:
            segments.append({
                "text": snippet.text,
                "start": round(snippet.start, 2),
                "duration": round(snippet.duration, 2)
            })

        return {
            "success": True,
            "video_id": video_id,
            "language": selected_transcript.language,
            "language_code": selected_transcript.language_code,
            "is_generated": selected_transcript.is_generated,
            "segment_count": len(segments),
            "transcript": segments
        }

    except TranscriptsDisabled:
        return {
            "success": False,
            "video_id": video_id,
            "error": "Transcripts are disabled for this video."
        }

    except VideoUnavailable:
        return {
            "success": False,
            "video_id": video_id,
            "error": "Video is unavailable."
        }

    except Exception as e:
        return {
            "success": False,
            "video_id": video_id,
            "error": str(e)
        }


if __name__ == "__main__":

    if len(sys.argv) < 2:
        print(json.dumps({
            "success": False,
            "error": "YouTube video ID is required."
        }))
        sys.exit(1)

    # Java passes the video ID here
    video_id = sys.argv[1]

    result = get_transcript(video_id)

    # Print JSON so Java can read it as a String
    print(json.dumps(result, ensure_ascii=False))