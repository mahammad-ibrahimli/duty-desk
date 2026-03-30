package com.example.dutydesk.dto.request.shift;

import jakarta.validation.constraints.NotBlank;

public record AddShiftNoteRequest(
    @NotBlank(message = "Content is required")
    String content
) {}
