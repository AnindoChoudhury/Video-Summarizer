package com.anindo.videosegment.controller;

import com.anindo.videosegment.dto.VideoSubmissionRequest;
import com.anindo.videosegment.dto.VideoSubmissionResponse;
import com.anindo.videosegment.service.VideoProcessingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/videos")
@CrossOrigin(origins = "http://localhost:3000")
public class VideoController {
    @Autowired
    private VideoProcessingService videoProcessingService;

    @PostMapping("/process")
    public ResponseEntity<VideoSubmissionResponse> processVideo(@Valid @RequestBody VideoSubmissionRequest videoBody){
        try {
            VideoSubmissionResponse response = videoProcessingService.saveVideo(videoBody.getUrl());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch(Exception e){
            e.printStackTrace();
            VideoSubmissionResponse errorResponse = new VideoSubmissionResponse(null,"FAILED", e.getMessage(), null);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
