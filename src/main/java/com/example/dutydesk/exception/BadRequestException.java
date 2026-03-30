package com.example.dutydesk.exception;

public class BadRequestException extends RuntimeException {
    private final Object details;

    public BadRequestException(String message) {
        this(message, null);
    }

    public BadRequestException(String message, Object details) {
        super(message);
        this.details = details;
    }

    public Object getDetails() {
        return details;
    }
}
