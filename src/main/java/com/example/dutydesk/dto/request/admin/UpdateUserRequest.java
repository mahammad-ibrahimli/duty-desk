package com.example.dutydesk.dto.request.admin;

import jakarta.validation.constraints.Pattern;
import java.util.UUID;

public record UpdateUserRequest(
        String firstName,
        String lastName,
        String role,
        UUID teamId,
        @Pattern(regexp = "^\\+994\\d{9}$", message = "phone must be in format +994XXXXXXXXX") String phone,
        Boolean isActive) {
}
