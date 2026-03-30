package com.example.dutydesk.dto.request.admin;

import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record UpsertScheduleRequest(
        @NotEmpty(message = "shifts is required") List<ShiftInput> shifts) {
    public record ShiftInput(UUID userId, LocalDate date, String type) {
    }
}
