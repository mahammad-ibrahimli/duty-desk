package com.example.dutydesk.dto.request.shift;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CheckInRequest(
        @NotNull(message = "shiftId is required") UUID shiftId,
        String note) {
}
