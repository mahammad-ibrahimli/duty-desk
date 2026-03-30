package com.example.dutydesk.dto.response.shift;

import java.util.UUID;

public record ShiftItemDto(UUID id,
        UUID userId,
        String userName,
        String teamName,
        String type,
        String typeLabel,
        String date,
        String startTime,
        String endTime,
        String status,
        CheckinDto checkin) {
    public record CheckinDto(String checkInTime, String checkOutTime, String status) {
    }
}
