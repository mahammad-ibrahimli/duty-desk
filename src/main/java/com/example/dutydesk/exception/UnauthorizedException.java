package com.example.dutydesk.exception;

public class UnauthorizedException extends RuntimeException {
    private final Object details;

    public UnauthorizedException(String message) {
        this(message, null);
    }

    public UnauthorizedException(String message, Object details) {
        super(message);
        this.details = details;
    }

    public Object getDetails() {
        return details;
    }
}
