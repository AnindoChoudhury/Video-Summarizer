package com.anindo.videosegment.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class GetTranscriptService {
    public String getTranscript(String videoId) {

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "python",
                    "Youtube_Transcript_Service/app.py",
                    videoId
            );

            Process process = processBuilder.start();

            // Read entire JSON output from Python
            String jsonOutput = new String(
                    process.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            String errorOutput = new String(
                    process.getErrorStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            // Wait for Python script to finish
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException(
                        "Python script failed: " + errorOutput
                );
            }

            return jsonOutput;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to fetch transcript: " + e.getMessage()
            );
        }
    }
}
