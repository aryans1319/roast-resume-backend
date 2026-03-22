package com.roastmyresume.backend.controller;

import com.roastmyresume.backend.dto.RoastResponse;
import com.roastmyresume.backend.exception.RoastException;
import com.roastmyresume.backend.service.RateLimiterService;
import com.roastmyresume.backend.service.RoastService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final RoastService roastService;
    private final RateLimiterService rateLimiterService;

    @PostMapping(value = "/roast", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RoastResponse> roastResume(
            @RequestPart("resume") MultipartFile resume,
            @RequestPart("jobDescription") String jobDescription,
            HttpServletRequest request) {

        String clientIp = getClientIp(request);
        log.info("Roast request from IP: {}", clientIp);

        if (!rateLimiterService.isAllowed(clientIp)) {
            log.warn("Rate limit exceeded for IP: {}", clientIp);
            throw new RoastException("You've used all 5 free roasts for this hour. Come back later! 🔥", true);
        }

        log.info("Remaining tokens for IP {}: {}", clientIp, rateLimiterService.getRemainingTokens(clientIp));
        RoastResponse response = roastService.roastResume(resume, jobDescription);
        return ResponseEntity.ok(response);
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}