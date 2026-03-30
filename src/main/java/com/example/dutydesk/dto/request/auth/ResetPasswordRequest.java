package com.example.dutydesk.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "resetToken is required") String resetToken,

        @NotBlank(message = "newPassword is required") @Size(min = 6, message = "Password must be at least 6 characters") String newPassword,

        @NotBlank(message = "confirmPassword is required") String confirmPassword) {
}
