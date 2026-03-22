package com.roastmyresume.backend.service;

import com.roastmyresume.backend.exception.PromptInjectionException;
import com.roastmyresume.backend.exception.RoastException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.DOTALL;

@Slf4j
@Service
public class SanitizationService {

    private static final int MAX_INPUT_LENGTH = 15_000;

    // Compiled once at startup — regex is expensive to compile repeatedly
    private static final List<Pattern> INJECTION_PATTERNS = List.of(
            Pattern.compile("ignore\\s+(previous|all|above)\\s+instructions?", CASE_INSENSITIVE),
            Pattern.compile("act\\s+as\\s+(a|an|if)", CASE_INSENSITIVE),
            Pattern.compile("you\\s+are\\s+now", CASE_INSENSITIVE),
            Pattern.compile("system\\s*prompt", CASE_INSENSITIVE),
            Pattern.compile("new\\s+instructions?", CASE_INSENSITIVE),
            Pattern.compile("forget\\s+(everything|all|previous)", CASE_INSENSITIVE),
            Pattern.compile("disregard\\s+(previous|all|above)", CASE_INSENSITIVE),
            Pattern.compile("jailbreak", CASE_INSENSITIVE),
            Pattern.compile("\\{\\{.*?\\}\\}", DOTALL),       // template injection: {{...}}
            Pattern.compile("<\\s*script.*?>", CASE_INSENSITIVE) // script tag injection
    );

    /**
     * Sanitizes untrusted input (resume text or job description) before
     * it is embedded in an LLM prompt.
     *
     * Throws PromptInjectionException (→ 400) if injection is detected.
     * Truncates silently if input exceeds MAX_INPUT_LENGTH.
     */
    public String sanitize(String input, String fieldName) {
        if (input == null || input.isBlank()) {
            throw new RoastException(fieldName + " cannot be empty.");
        }

        // Truncate to safe length before scanning
        String truncated = input.length() > MAX_INPUT_LENGTH
                ? input.substring(0, MAX_INPUT_LENGTH)
                : input;

        for (Pattern pattern : INJECTION_PATTERNS) {
            if (pattern.matcher(truncated).find()) {
                log.warn("Prompt injection attempt detected in [{}]. Pattern: {}",
                        fieldName, pattern.pattern());
                // Don't reveal which pattern matched — just reject
                throw new PromptInjectionException(
                        "Invalid content detected in " + fieldName + ". Please check your input.");
            }
        }

        return truncated.trim();
    }
}