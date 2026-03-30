package com.example.dutydesk.exception;

public class ValidationException extends RuntimeException {
    private final Object details;

    public ValidationException(String message) {
        this(message, null);
    }

    public ValidationException(String message, Object details) {
        super(message);
        this.details = details;
    }

    public Object getDetails() {
        return details;
    }
}
