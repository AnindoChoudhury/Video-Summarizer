package com.anindo.videosegment.service;

import com.anindo.videosegment.dto.VideoSubmissionResponse;
import com.anindo.videosegment.entity.Video;
import com.anindo.videosegment.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class VideoProcessingService {

    private VideoRepository videoRepository;
    private RedisTemplate<String,Object> redisTemplate;
    private GeminiService geminiService;
    private YouTubeTranscriptService youTubeTranscriptService;

    @Autowired
    VideoProcessingService(VideoRepository videoRepository, RedisTemplate<String,Object> redisTemplate, GeminiService geminiService, YouTubeTranscriptService youTubeTranscriptService){
        this.videoRepository = videoRepository;
        this.redisTemplate = redisTemplate;
        this.geminiService = geminiService;
        this.youTubeTranscriptService = youTubeTranscriptService;
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


        String transcript = youTubeTranscriptService.getTranscript(videoID);

        if(cachedVideo != null){
            return new VideoSubmissionResponse(videoID,cachedVideo.getStatus(), "Video found in cache");
        }

        Optional<Video> existingVideo = videoRepository.findById(videoID);

        if(existingVideo.isPresent()){
            Video video = existingVideo.get();
            redisTemplate.opsForValue().set(redisKey,video,Duration.ofMinutes(30));
            return new VideoSubmissionResponse(video.getVideoID(),video.getStatus(),"Video found in DB");
        }

        Video newVideo = new Video();
        newVideo.setUrl(url);
        newVideo.setStatus("PROCESSING");
        newVideo.setVideoID(videoID);

        // Send to gemini LLM
        try{
            String response = geminiService.callGeminiAPI(transcript);
            newVideo.setStatus("COMPLETED");
            newVideo.setSegments(response.);
            System.out.println(response);
//            return new VideoSubmissionResponse(newVideo.getVideoID(), newVideo.getStatus(), "Completed analysis");
        }
        catch(Exception e){
            markAsFailed(newVideo);
            throw new RuntimeException("Cannot process the video");
        }

        videoRepository.save(newVideo);

        // Save the video in redis
        redisTemplate.opsForValue().set(redisKey,newVideo,Duration.ofMinutes(30));

        // (Post office name, address, Message we want to send)
//        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME,RabbitMQConfig.ROUTING_KEY,videoID);

        String message = (newVideo.getStatus().equals("COMPLETED") ? "Video analysis complete" : "Your video is being processed");
        return new VideoSubmissionResponse(videoID, newVideo.getStatus(), message);
    }
}
