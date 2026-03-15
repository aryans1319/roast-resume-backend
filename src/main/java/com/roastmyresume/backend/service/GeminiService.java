package com.roastmyresume.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roastmyresume.backend.config.AppConfig;
import com.roastmyresume.backend.exception.RoastException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {

    private final AppConfig appConfig;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    public String getRoastFromGemini(String resumeText, String jobDescription) {
        String prompt = buildPrompt(resumeText, jobDescription);
        Map<String, Object> requestBody = buildRequestBody(prompt);

        try {
            String response = webClientBuilder.build()
                    .post()
                    .uri(appConfig.getApiUrl() + "?key=" + appConfig.getApiKey())
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return extractTextFromResponse(response);
        } catch (Exception e) {
            log.error("Gemini API call failed", e);
            throw new RoastException("Failed to analyze your resume. Please try again.");
        }
    }

    private String extractTextFromResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            return root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
        } catch (Exception e) {
            throw new RoastException("Failed to parse Gemini response.");
        }
    }

    private Map<String, Object> buildRequestBody(String prompt) {
        return Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{
                                Map.of("text", prompt)
                        })
                },
                "generationConfig", Map.of(
                        "maxOutputTokens", appConfig.getMaxTokens(),
                        "temperature", 0.7
                )
        );
    }

    private String buildPrompt(String resumeText, String jobDescription) {
        return """
                You are a brutally honest but constructive senior tech recruiter with 15 years of experience.
                Analyze the resume against the job description and respond ONLY with a valid JSON object.
                No markdown, no backticks, no explanation outside the JSON.

                Scoring rules:
                - impactScore: Do bullet points show measurable results or just duties?
                - keywordMatch: How well does resume match the job description keywords?
                - credibilityCheck: Are listed skills actually demonstrated in experience/projects?
                - atsFriendliness: Will ATS systems parse this correctly?

                Each score is out of 100. Be harsh but fair.

                Return this exact JSON structure:
                {
                  "overallScore": <number 0-100>,
                  "savageOneLiner": "<one brutally honest sentence about this resume>",
                  "impactScore": {
                    "score": <number>,
                    "feedback": "<specific feedback>",
                    "fixedExample": "<rewritten example bullet point>"
                  },
                  "keywordMatch": {
                    "score": <number>,
                    "feedback": "<specific feedback>",
                    "fixedExample": "<example of better keyword usage>"
                  },
                  "credibilityCheck": {
                    "score": <number>,
                    "feedback": "<specific feedback>",
                    "fixedExample": "<how to demonstrate a skill properly>"
                  },
                  "atsFriendliness": {
                    "score": <number>,
                    "feedback": "<specific feedback>",
                    "fixedExample": "<formatting suggestion>"
                  },
                  "topIssue1": "<most critical issue to fix>",
                  "topIssue2": "<second most critical issue>",
                  "topIssue3": "<third most critical issue>",
                  "rewrittenSummary": "<a rewritten professional summary for this person>"
                }

                Resume:
                %s

                Job Description:
                %s
                """.formatted(resumeText, jobDescription);
    }
}

//Random change :)