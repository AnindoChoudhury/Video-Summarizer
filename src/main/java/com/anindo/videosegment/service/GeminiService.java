package com.anindo.videosegment.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Schema;
import com.google.genai.types.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GeminiService {

    private GetTranscriptService getTranscriptService;
    private final Client client;

    @Autowired
    GeminiService(@Value("${gemini.api.key}") String apiKey, GetTranscriptService getTranscriptService){
        this.client = Client.builder().apiKey(apiKey).build();
        this.getTranscriptService = getTranscriptService;
    }

    public String callGeminiAPI(String transcript) {

        String prompt = "Create logical video chapters/segments for a YouTube video with transcript: " + transcript +
                ". Respond ONLY with a valid JSON array of objects. " +
                "Do not use markdown blocks. Intelligently divide the video into " +
                "major thematic segments only where the topic shifts significantly." +
                "Avoid creating tiny, fragmented segments; focus on substantial 'learning blocks'." +
                "Each object must have these exact keys: 'title', 'summary', 'startTimeSeconds' (Double), 'endTimeSeconds' (Double). " +
                "Do not enclose startTimeSeconds and endTimeSeconds in quotes";

        Schema responseSchema = Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(
                        Map.of(
                                "videoSegments", Schema.builder().type(Type.Known.ARRAY).items(
                                        Schema.builder().type(Type.Known.OBJECT).properties(
                                                        Map.of(
                                                                "topic", Schema.builder().type(Type.Known.STRING).description("Topic name of the segment").build(),
                                                                "summary", Schema.builder().type(Type.Known.STRING).description("Short detailed easy to grasp summary of the segment").build(),
                                                                "startTimeSeconds", Schema.builder().type(Type.Known.STRING).build(),
                                                                "endTimeSeconds", Schema.builder().type(Type.Known.STRING).build()
                                                        )).required(java.util.List.of("topic", "summary", "startTimeSeconds", "endTimeSeconds")).build()
                                        ).build()
                                )).required(java.util.List.of("videoSegments")).build();


        // Configure the model to output according to the schema
        GenerateContentConfig config = GenerateContentConfig.builder()
                .responseMimeType("application/json")
                .responseSchema(responseSchema)
                .build();


        // Calling the API
        GenerateContentResponse response = client.models.generateContent(
                "gemini-3.5-flash",
                prompt,
                config
        );

        return response.text();
    }

}


