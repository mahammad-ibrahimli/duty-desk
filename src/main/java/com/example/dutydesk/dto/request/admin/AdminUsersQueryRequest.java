package com.example.dutydesk.dto.request.admin;

import java.util.UUID;

public record AdminUsersQueryRequest(
        String role,
        UUID team,
        String search,
        String status) {
}
