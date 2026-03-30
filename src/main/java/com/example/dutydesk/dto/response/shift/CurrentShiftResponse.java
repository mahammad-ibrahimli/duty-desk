package com.example.dutydesk.dto.response.shift;

import java.util.UUID;

public record CurrentShiftResponse(UUID id,
        String type,
        java.time.Instant startTime,
        java.time.Instant endTime,
        long remainingTime,
        String status,
        CheckinInfo checkin) {

    public record CheckinInfo(java.time.Instant checkInTime, String status) {
    }
}
