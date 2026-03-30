package com.example.dutydesk.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyCodeRequest(
        @NotBlank(message = "Email is required") @Email(message = "Email format is invalid") String email,

        @NotBlank(message = "Code is required") @Pattern(regexp = "^[0-9]{6}$", message = "Code must be 6 digits") String code) {
}
