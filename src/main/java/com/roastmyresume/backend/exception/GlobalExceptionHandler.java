package com.roastmyresume.backend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RoastException.class)
    public ResponseEntity<Map<String, String>> handleRoastException(RoastException ex) {
        HttpStatus status = ex.isRateLimitExceeded()
                ? HttpStatus.TOO_MANY_REQUESTS   // 429
                : HttpStatus.BAD_REQUEST;         // 400
        return ResponseEntity.status(status)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(PromptInjectionException.class)
    public ResponseEntity<Map<String, String>> handlePromptInjection(PromptInjectionException ex) {
        log.warn("Prompt injection blocked: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxSizeException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "File size exceeds the 5MB limit."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Something went wrong. Please try again."));
    }
}