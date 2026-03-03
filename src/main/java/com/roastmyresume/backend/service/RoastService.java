package com.roastmyresume.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roastmyresume.backend.dto.RoastResponse;
import com.roastmyresume.backend.exception.RoastException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoastService {

    private final ResumeParserService resumeParserService;
    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;

    public RoastResponse roastResume(MultipartFile resumeFile, String jobDescription) {
        // Step 1: Extract text from PDF
        String resumeText = resumeParserService.extractTextFromPdf(resumeFile);

        // Step 2: Get roast from Gemini
        String geminiResponse = geminiService.getRoastFromGemini(resumeText, jobDescription);

        // Step 3: Parse JSON response into our DTO
        return parseRoastResponse(geminiResponse);
    }

    private RoastResponse parseRoastResponse(String jsonResponse) {
        try {
            return objectMapper.readValue(jsonResponse, RoastResponse.class);
        } catch (Exception e) {
            log.error("Failed to parse roast response: {}", jsonResponse, e);
            throw new RoastException("Failed to process the analysis. Please try again.");
        }
    }
}