package com.anindo.videosegment.dto;

import com.anindo.videosegment.entity.Video;
import lombok.Data;

@Data
public class VideoSubmissionResponse {
    private String videoID;
    private String status;
    private String message;

    public VideoSubmissionResponse(String videoID, String status, String message){
        this.videoID = videoID;
        this.status = status;
        this.message = message;
    }
}
