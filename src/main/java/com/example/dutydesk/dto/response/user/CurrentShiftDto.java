package com.example.dutydesk.dto.response.user;

import java.util.UUID;

public record CurrentShiftDto(UUID id,
        String type,
        java.time.Instant startTime,
        java.time.Instant endTime,
        String status) {
}
