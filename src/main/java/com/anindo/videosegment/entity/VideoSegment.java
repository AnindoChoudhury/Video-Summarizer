package com.anindo.videosegment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "video_segments")
@Data
public class VideoSegment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "summary", length = 1000)
    private String summary;

    @Column(name = "start_time_seconds", nullable = false)
    private Integer startTimeSeconds;

    @Column(name = "end_time_seconds", nullable = false)
    private Integer endTimeSeconds;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    @JsonIgnore
    private Video video;
}
