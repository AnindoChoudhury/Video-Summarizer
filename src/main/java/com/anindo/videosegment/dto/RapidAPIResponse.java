package com.anindo.videosegment.dto;

import lombok.Data;

import java.util.List;

public class RapidAPIResponse {


    @Data
    public static class Root {
        private List<String> availableLangs;

        private String lengthInSeconds;

        private List<Transcription> transcription;
    }



    @Data
    public static class Transcription {
        private String subtitle;

        private Double start;

        private Double dur;
    }
}
