package com.example.dutydesk.exception;

public class ResourceNotFoundException extends RuntimeException {
    private final Object details;

    public ResourceNotFoundException(String message) {
        this(message, null);
    }

    public ResourceNotFoundException(String message, Object details) {
        super(message);
        this.details = details;
    }

    public Object getDetails() {
        return details;
    }
}
