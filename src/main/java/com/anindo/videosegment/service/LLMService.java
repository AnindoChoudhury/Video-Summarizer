package com.anindo.videosegment.service;


import com.anindo.videosegment.entity.VideoSegment;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LLMService {

    private final Client client;
    private final ObjectMapper objectMapper;
    private final String modelName = "gemini-3.5-flash";

    public LLMService(@Value("${gemini.api.key}") String apiKey, ObjectMapper objectMapper) {
        this.client = Client.builder().apiKey(apiKey).build();
        this.objectMapper = objectMapper;
    }

    public List<VideoSegment> generateChapters(String videoId) {
        String prompt = "Create logical video chapters/segments for a YouTube video with ID: " + videoId + ". " +
                "Respond ONLY with a valid JSON array of objects. Do not use markdown blocks. Summarise each segment" +
                "Each object must have these exact keys: 'title', 'summary', 'startTimeSeconds' (int), 'endTimeSeconds' (int). " +
                "Ensure startTimeSeconds starts at 0 and increments logically.";

        try {


            GenerateContentResponse response = client.models.generateContent(modelName, prompt, null);

            String llmJsonOutput = response.text();

            if (llmJsonOutput != null) {
                llmJsonOutput = llmJsonOutput.replace("```json", "")
                        .replace("```", "")
                        .trim();
            }

            return objectMapper.readValue(llmJsonOutput, new TypeReference<List<VideoSegment>>() {
            });

        } catch (Exception e) {
            System.err.println("Gemini SDK Error: " + e.getMessage());
            throw new RuntimeException("Failed to generate segments from Gemini", e);
        }
    }
}