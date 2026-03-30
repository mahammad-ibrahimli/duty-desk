package com.example.dutydesk.dto.response.user;

import java.util.UUID;

public record CurrentUserResponse(UUID id,
        String email,
        String firstName,
        String lastName,
        String role,
        String phone,
        TeamSummaryDto team,
        CurrentShiftDto currentShift) {
}
