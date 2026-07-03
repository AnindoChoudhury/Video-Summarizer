package com.anindo.videosegment.service;

import com.anindo.videosegment.config.RabbitMQConfig;
import com.anindo.videosegment.dto.VideoSubmissionResponse;
import com.anindo.videosegment.entity.Video;
import com.anindo.videosegment.repository.VideoRepository;
import com.anindo.videosegment.worker.VideoProcessingWorker;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class VideoProcessingService {

    private VideoRepository videoRepository;
    private RabbitTemplate rabbitTemplate;
    private RedisTemplate<String,Video> redisTemplate;

    @Autowired
    VideoProcessingService(VideoRepository videoRepository, RabbitTemplate rabitTemplate, RedisTemplate<String,Video> redisTemplate){
        this.videoRepository = videoRepository;
        this.rabbitTemplate = rabitTemplate;
        this.redisTemplate = redisTemplate;
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

    public VideoSubmissionResponse saveVideo(String url) {
        String videoID = extractVideoIdFromUrl(url);
        String redisKey = "video:"+videoID;
        Video cachedVideo = (Video) redisTemplate.opsForValue().get(redisKey);
        if(cachedVideo != null){
            return new VideoSubmissionResponse(videoID,cachedVideo.getStatus(), "Video found in cache");
        }

        Optional<Video> existingVideo = videoRepository.findById(videoID);

        if(existingVideo.isPresent()){
            Video video = existingVideo.get();
            return new VideoSubmissionResponse(video.getVideoID(),video.getStatus(),"Video found in DB");
        }

        Video newVideo = new Video();
        newVideo.setUrl(url);
        newVideo.setStatus("PROCESSING");
        newVideo.setVideoID(videoID);

        videoRepository.save(newVideo);

        // Save the video in redis
        redisTemplate.opsForValue().set(redisKey,newVideo, Duration.ofMinutes(30));

        // (Post office name, address, Message we want to send)
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME,RabbitMQConfig.ROUTING_KEY,videoID);

        return new VideoSubmissionResponse(videoID, "PROCESSING", "Your video is being processed");
    }
}
