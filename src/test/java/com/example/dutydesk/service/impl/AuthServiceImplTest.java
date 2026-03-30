package com.example.dutydesk.service.impl;

import com.example.dutydesk.dto.request.auth.ForgotPasswordRequest;
import com.example.dutydesk.dto.request.auth.LoginRequest;
import com.example.dutydesk.dto.request.auth.ResetPasswordRequest;
import com.example.dutydesk.dto.response.auth.ForgotPasswordResponse;
import com.example.dutydesk.entities.PasswordResetToken;
import com.example.dutydesk.entities.User;
import com.example.dutydesk.exception.BadRequestException;
import com.example.dutydesk.exception.UnauthorizedException;
import com.example.dutydesk.repository.PasswordResetTokenRepository;
import com.example.dutydesk.repository.UserRepository;
import com.example.dutydesk.securty.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "jwtExpirationMs", 86400000L);
        ReflectionTestUtils.setField(authService, "resetCodeExpirationSeconds", 120L);
    }

    @Test
    void forgotPassword_WhenUserExists_SavesTokenAndReturnsExpiry() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("admin123@example.com")
                .passwordHash("hash")
                .firstName("Admin")
                .lastName("User")
                .build();
        when(userRepository.findByEmail("admin123@example.com")).thenReturn(Optional.of(user));

        ForgotPasswordResponse response = authService.forgotPassword(new ForgotPasswordRequest("admin123@example.com"));

        assertEquals(120L, response.expiresIn());
        ArgumentCaptor<PasswordResetToken> captor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(passwordResetTokenRepository).save(captor.capture());
        PasswordResetToken saved = captor.getValue();
        assertEquals(user, saved.getUser());
        assertNotNull(saved.getToken());
        assertEquals(6, saved.getToken().length());
        assertFalse(saved.isUsed());
    }

    @Test
    void login_WhenCredentialsInvalid_ThrowsUnauthorized() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(UnauthorizedException.class,
                () -> authService.login(new LoginRequest("admin123@example.com", "wrong-password")));
    }

    @Test
    void resetPassword_WhenPasswordsDoNotMatch_ThrowsBadRequest() {
        ResetPasswordRequest request = new ResetPasswordRequest("token", "new-password", "different-password");

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> authService.resetPassword(request));

        assertEquals("Password and confirmPassword do not match", exception.getMessage());
    }
}
