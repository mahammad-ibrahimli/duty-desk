package com.example.dutydesk.dto.response.auth;

import java.util.UUID;

public record LoginResponse(UserDto user, String token, long expiresIn) {
    public record UserDto(UUID id,
            String email,
            String firstName,
            String lastName,
            String role,
            TeamDto team) {
    }

    public record TeamDto(UUID id, String name) {
    }
}
