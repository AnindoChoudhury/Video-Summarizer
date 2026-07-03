package com.anindo.videosegment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VideoSubmissionRequest {
    @NotBlank(message = "URL cannot be blank")
    private String url;
}
