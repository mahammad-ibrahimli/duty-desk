package com.example.dutydesk.dto.request.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record CreateUserRequest(
        @NotBlank(message = "email is required") @Email(message = "email format is invalid") String email,

        @NotBlank(message = "firstName is required") String firstName,

        @NotBlank(message = "lastName is required") String lastName,

        @NotBlank(message = "role is required") String role,

        @NotNull(message = "teamId is required") UUID teamId,

        @Pattern(regexp = "^\\+994\\d{9}$", message = "phone must be in format +994XXXXXXXXX") String phone,

        @NotBlank(message = "password is required") String password) {
}
