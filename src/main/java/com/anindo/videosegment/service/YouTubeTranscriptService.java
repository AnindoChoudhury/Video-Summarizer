package com.anindo.videosegment.service;


import com.anindo.videosegment.dto.RapidAPIResponse;
import com.anindo.videosegment.entity.Video;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class YouTubeTranscriptService {

    @Value("${rapidapi.key}")
    private String rapidApiKey;
    @Value("${rapidapi.host}")
    private String rapidAPIHost;

    private final RestTemplate restTemplate;

    YouTubeTranscriptService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public String getTranscript(String videoId){
        String rapidApiEndpoint = "https://" + rapidAPIHost + "/transcript?video_id=" + videoId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", rapidApiKey);
        headers.set("X-RapidAPI-Host", rapidAPIHost);

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<RapidAPIResponse.Root[]> response = restTemplate.exchange(
                    rapidApiEndpoint,
                    HttpMethod.GET,
                    httpEntity,
                    RapidAPIResponse.Root[].class
            );

            // Extract the Root object
            RapidAPIResponse.Root[] rootArray = response.getBody();
            StringBuilder formattedTranscript = new StringBuilder();

            // Check for nulls, then loop through the transcription list!
            if (rootArray != null && rootArray.length > 0) {
                for (RapidAPIResponse.Transcription line : rootArray[0].getTranscription()) {

                    formattedTranscript.append("[")
                            .append(line.getStart()).append("s - ")
                            .append(line.getStart() + line.getDur()).append("s]\n")
                            .append(line.getSubtitle()).append("\n");
                }
            }

            return formattedTranscript.toString();

        } catch (Exception e) {
            System.out.println("Wrong");
            throw new RuntimeException("Failed to fetch transcript from RapidAPI: " + e.getMessage(), e);
        }
    }
}
