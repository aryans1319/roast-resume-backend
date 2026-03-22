package com.roastmyresume.backend.exception;

import lombok.Getter;

@Getter
public class RoastException extends RuntimeException {

    private final boolean rateLimitExceeded;

    public RoastException(String message) {
        super(message);
        this.rateLimitExceeded = false;
    }

    public RoastException(String message, boolean rateLimitExceeded) {
        super(message);
        this.rateLimitExceeded = rateLimitExceeded;
    }
}