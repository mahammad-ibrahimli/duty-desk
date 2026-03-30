package com.example.dutydesk.dto.response.shift;

import java.util.UUID;

public record CheckInOutResponse(UUID checkinId,
        java.time.Instant checkInTime,
        java.time.Instant checkOutTime,
        Double totalHours) {
}
