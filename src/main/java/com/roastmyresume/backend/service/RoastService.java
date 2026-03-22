package com.roastmyresume.backend.service;

import com.roastmyresume.backend.dto.RoastResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoastService {

    private final ResumeParserService resumeParserService;
    private final SanitizationService sanitizationService;
    private final GeminiService geminiService;

    public RoastResponse roastResume(MultipartFile resumeFile, String jobDescription) {
        // Step 1: Extract raw text from PDF
        String resumeText = resumeParserService.extractTextFromPdf(resumeFile);

        // Step 2: Sanitize BOTH inputs — reject if injection detected
        String cleanResume = sanitizationService.sanitize(resumeText, "resume");
        String cleanJD = sanitizationService.sanitize(jobDescription, "jobDescription");

        log.info("Sending sanitized inputs to Gemini. Resume length: {}, JD length: {}",
                cleanResume.length(), cleanJD.length());

        // Step 3: Call Gemini
        String geminiRawResponse = geminiService.getRoastFromGemini(cleanResume, cleanJD);

        // Step 4: Parse + validate into typed DTO
        return geminiService.extractAndValidate(geminiRawResponse, RoastResponse.class);
    }
}