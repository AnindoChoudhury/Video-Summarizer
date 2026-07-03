package com.anindo.videosegment.worker;

import com.anindo.videosegment.entity.Video;
import com.anindo.videosegment.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
public class VideoProcessingWorker {

    private VideoRepository videoRepository;
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    VideoProcessingWorker(VideoRepository videoRepository, RedisTemplate<String,Object> redisTemplate){
        this.videoRepository = videoRepository;
        this.redisTemplate = redisTemplate;
    }


    // Once a videoID enters queue, this method wakes up and starts executing in background
//    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
//    public void requestVideoMessage(String videoId){
//        try{
//            Optional<Video> optionalVideo = videoRepository.findById(videoId);
//            if(optionalVideo.isEmpty()){
//                System.out.println("Video not found in DB");
//                return;
//            }
//            Video video = optionalVideo.get();
//
//            System.out.println("Simulating API call");
//            Thread.sleep(5000);
//
//            VideoSegment segment1 = new VideoSegment();
//            segment1.setTitle("Introduction and Hook");
//            segment1.setSummary("The creator introduces the main topic of the video.");
//            segment1.setStartTimeSeconds(0);
//            segment1.setEndTimeSeconds(120); // 0:00 to 2:00
//            segment1.setVideo(video);
//
//            VideoSegment segment2 = new VideoSegment();
//            segment2.setTitle("Deep Dive into Core Concepts");
//            segment2.setSummary("Detailed explanation and visual examples.");
//            segment2.setStartTimeSeconds(121);
//            segment2.setEndTimeSeconds(450); // 2:01 to 7:30
//            segment2.setVideo(video);
//
//            video.getSegments().add(segment1);
//            video.getSegments().add(segment2);
//
//            video.setStatus("COMPLETED");
//
//            videoRepository.save(video);
//        }
//        catch(Exception e){
//            System.out.println("An error occured");
//            markVideoFailed(videoId);
//        }
//    }

    private void markVideoFailed(String videoId) {
        Optional<Video> optionalVideo = videoRepository.findById(videoId);
        if (optionalVideo.isPresent()) {
            Video video = optionalVideo.get();
            video.setStatus("FAILED");
            videoRepository.save(video);

            String redisKey = "video:" + videoId;
            redisTemplate.opsForValue().set(redisKey, video, Duration.ofHours(24));
        }
    }
}
