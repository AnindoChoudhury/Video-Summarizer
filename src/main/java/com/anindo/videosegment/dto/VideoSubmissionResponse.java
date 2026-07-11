package com.anindo.videosegment.dto;

import com.anindo.videosegment.entity.Video;
import com.anindo.videosegment.entity.VideoSegment;
import lombok.Data;

import java.util.List;

@Data
public class VideoSubmissionResponse {
    private String videoID;
    private String status;
    private String message;
    private List<VideoSegment> videoSegments;
    public VideoSubmissionResponse(String videoID, String status, String message, List<VideoSegment> videoSegments){
        this.videoID = videoID;
        this.status = status;
        this.message = message;
        this.videoSegments = videoSegments;
    }
}
