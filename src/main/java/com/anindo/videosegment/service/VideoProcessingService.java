package com.anindo.videosegment.service;

import com.anindo.videosegment.dto.VideoSubmissionResponse;
import com.anindo.videosegment.entity.Video;
import com.anindo.videosegment.entity.VideoSegment;
import com.anindo.videosegment.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Optional;

@Service
public class VideoProcessingService {

    private VideoRepository videoRepository;
    private RedisTemplate<String,Object> redisTemplate;
    private GeminiService geminiService;
    private GetTranscriptService getTranscriptService;

    @Autowired
    VideoProcessingService(VideoRepository videoRepository, RedisTemplate<String,Object> redisTemplate, GeminiService geminiService, GetTranscriptService getTranscriptService){
        this.videoRepository = videoRepository;
        this.redisTemplate = redisTemplate;
        this.geminiService = geminiService;
        this.getTranscriptService = getTranscriptService;
    }

    private String extractVideoIdFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL cannot be empty");
        }

        String videoId = "";

        if (url.contains("v=")) {
            int startIndex = url.indexOf("v=") + 2;
            int endIndex = url.indexOf("&", startIndex);

            if (endIndex == -1) {
                videoId = url.substring(startIndex);
            } else {
                videoId = url.substring(startIndex, endIndex);
            }
        } else if (url.contains("youtu.be/")) {
            int startIndex = url.indexOf("youtu.be/") + 9;
            int endIndex = url.indexOf("?", startIndex);

            if (endIndex == -1) {
                videoId = url.substring(startIndex);
            } else {
                videoId = url.substring(startIndex, endIndex);
            }
        } else {
            throw new IllegalArgumentException("Invalid YouTube URL format");
        }

        return videoId;
    }

    public void markAsFailed(Video video) throws RuntimeException{
        video.setStatus("FAILED");
        videoRepository.save(video);
    }


    public VideoSubmissionResponse saveVideo(String url) {
        String videoID = extractVideoIdFromUrl(url);
        String redisKey = "video:"+videoID;
        Video cachedVideo = (Video) redisTemplate.opsForValue().get(redisKey);


        if(cachedVideo != null){
            return new VideoSubmissionResponse(videoID,cachedVideo.getStatus(), "Video found in cache", cachedVideo.getSegments());
        }

        Optional<Video> existingVideo = videoRepository.findById(videoID);

        if(existingVideo.isPresent()){
            Video video = existingVideo.get();
            redisTemplate.opsForValue().set(redisKey,video,Duration.ofMinutes(30));
            return new VideoSubmissionResponse(video.getVideoID(),video.getStatus(),"Video found in DB", video.getSegments());
        }

        Video newVideo = new Video();
        newVideo.setUrl(url);
        newVideo.setStatus("PROCESSING");
        newVideo.setVideoID(videoID);

//         Send to gemini LLM only when the video does not exist in DB
        try{
            // Get transcript
            String transcript = getTranscriptService.getTranscript(videoID);

            String response = geminiService.callGeminiAPI(transcript);

//            System.out.println("Transcript : " + transcript);
//
//            System.out.println("response : " + response);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode segmentsNode = rootNode.path("videoSegments");

            if(segmentsNode.isArray()){
                for(JsonNode node : segmentsNode){
                    VideoSegment videoSegment = new VideoSegment();
                    videoSegment.setTopic(node.path("topic").asText());
                    videoSegment.setSummary(node.path("summary").asText());
                    videoSegment.setStartTimeSeconds(node.path("startTimeSeconds").asDouble());
                    videoSegment.setEndTimeSeconds(node.path("endTimeSeconds").asDouble());
                    // Create many-to-one relationship
                    videoSegment.setVideo(newVideo);
                    newVideo.getSegments().add(videoSegment);
                }
            }
            newVideo.setStatus("COMPLETED");
//          return new VideoSubmissionResponse(newVideo.getVideoID(), newVideo.getStatus(), "Completed analysis", newVideo.getSegments());
        }
        catch(Exception e){
            e.printStackTrace();
            markAsFailed(newVideo);
            throw new RuntimeException("Cannot process the video");
        }

        videoRepository.save(newVideo);

        // Save the video in redis

        redisTemplate.opsForValue().set(redisKey,newVideo,Duration.ofMinutes(30));

        // (Post office name, address, Message we want to send)
//        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME,RabbitMQConfig.ROUTING_KEY,videoID);

        String message = (newVideo.getStatus().equals("COMPLETED") ? "Video analysis complete" : "Your video is being processed");
        return new VideoSubmissionResponse(videoID, newVideo.getStatus(), message, newVideo.getSegments());
    }
}
