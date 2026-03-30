package com.example.dutydesk.dto.request.shift;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CheckOutRequest(
        @NotNull(message = "shiftId is required") UUID shiftId,
        String note) {
}
