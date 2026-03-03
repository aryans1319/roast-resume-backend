package com.roastmyresume.backend.exception;

public class RoastException extends RuntimeException {
    public RoastException(String message) {
        super(message);
    }

    public RoastException(String message, Throwable cause) {
        super(message, cause);
    }
}