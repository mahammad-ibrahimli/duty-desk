package com.example.dutydesk.dto.response.common;

public record ApiErrorResponse(boolean success, ErrorBody error) {

    public ApiErrorResponse(String code, String message, Object details) {
        this(false, new ErrorBody(code, message, details));
    }

    public record ErrorBody(String code, String message, Object details) {
    }
}
