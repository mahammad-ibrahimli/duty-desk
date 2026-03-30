package com.example.dutydesk.controller;

import com.example.dutydesk.dto.request.auth.LoginRequest;
import com.example.dutydesk.dto.request.auth.ForgotPasswordRequest;
import com.example.dutydesk.dto.request.auth.ResetPasswordRequest;
import com.example.dutydesk.dto.request.auth.VerifyCodeRequest;
import com.example.dutydesk.dto.response.auth.ForgotPasswordResponse;
import com.example.dutydesk.dto.response.auth.LoginResponse;
import com.example.dutydesk.dto.response.auth.SimpleMessageResponse;
import com.example.dutydesk.dto.response.auth.VerifyCodeResponse;
import com.example.dutydesk.dto.response.common.ApiResponse;
import com.example.dutydesk.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/forgot-password")
    public ApiResponse<ForgotPasswordResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ApiResponse.success(authService.forgotPassword(request));
    }

    @PostMapping("/verify-code")
    public ApiResponse<VerifyCodeResponse> verifyCode(@Valid @RequestBody VerifyCodeRequest request) {
        return ApiResponse.success(authService.verifyCode(request));
    }

    @PostMapping("/reset-password")
    public ApiResponse<SimpleMessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return ApiResponse.success(authService.resetPassword(request));
    }
}
