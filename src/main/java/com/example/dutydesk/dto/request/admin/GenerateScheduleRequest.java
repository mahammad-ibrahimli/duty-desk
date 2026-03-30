package com.example.dutydesk.dto.request.admin;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GenerateScheduleRequest(
        @NotNull(message = "Team ID is required")
        UUID teamId,

        @Min(value = 2024, message = "Year must be valid")
        int year,

        @Min(value = 1, message = "Month must be between 1 and 12")
        @Max(value = 12, message = "Month must be between 1 and 12")
        int month
) {}
