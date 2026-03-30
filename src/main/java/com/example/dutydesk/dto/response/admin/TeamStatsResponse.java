package com.example.dutydesk.dto.response.admin;

import java.util.UUID;

public record TeamStatsResponse(
        UUID id,
        String name,
        String description,
        long memberCount,
        long activeShiftCount) {
}
