package com.example.dutydesk.controller;

import com.example.dutydesk.dto.request.auth.ForgotPasswordRequest;
import com.example.dutydesk.dto.request.auth.LoginRequest;
import com.example.dutydesk.dto.response.auth.ForgotPasswordResponse;
import com.example.dutydesk.dto.response.auth.LoginResponse;
import com.example.dutydesk.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void login_DelegatesToServiceAndReturnsApiResponse() {
        LoginRequest request = new LoginRequest("admin123@example.com", "password");
        LoginResponse response = new LoginResponse(
                new LoginResponse.UserDto(
                        UUID.randomUUID(),
                        "admin123@example.com",
                        "Admin",
                        "User",
                        "admin",
                        null),
                "jwt-token",
                86400L);

        when(authService.login(request)).thenReturn(response);

        LoginResponse result = authController.login(request).data();

        assertEquals("jwt-token", result.token());
        assertEquals("admin123@example.com", result.user().email());
        verify(authService).login(request);
    }

    @Test
    void forgotPassword_DelegatesToServiceAndReturnsExpiresIn() {
        ForgotPasswordRequest request = new ForgotPasswordRequest("admin123@example.com");
        ForgotPasswordResponse response = new ForgotPasswordResponse(120L);

        when(authService.forgotPassword(request)).thenReturn(response);

        ForgotPasswordResponse result = authController.forgotPassword(request).data();

        assertEquals(120L, result.expiresIn());
        verify(authService).forgotPassword(request);
    }
}
