package com.example.dutydesk.service.impl;

import com.example.dutydesk.dto.request.auth.LoginRequest;
import com.example.dutydesk.dto.request.auth.ForgotPasswordRequest;
import com.example.dutydesk.dto.request.auth.ResetPasswordRequest;
import com.example.dutydesk.dto.request.auth.VerifyCodeRequest;
import com.example.dutydesk.dto.response.auth.ForgotPasswordResponse;
import com.example.dutydesk.dto.response.auth.LoginResponse;
import com.example.dutydesk.dto.response.auth.SimpleMessageResponse;
import com.example.dutydesk.dto.response.auth.VerifyCodeResponse;
import com.example.dutydesk.entities.PasswordResetToken;
import com.example.dutydesk.entities.User;
import com.example.dutydesk.exception.BadRequestException;
import com.example.dutydesk.exception.UnauthorizedException;
import com.example.dutydesk.repository.PasswordResetTokenRepository;
import com.example.dutydesk.repository.UserRepository;
import com.example.dutydesk.securty.JwtService;
import com.example.dutydesk.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${app.security.jwt.expiration-ms:86400000}")
    private long jwtExpirationMs;

    @Value("${app.security.reset-code.expiration-seconds:120}")
    private long resetCodeExpirationSeconds;

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);

            User user = userRepository.findByEmail(request.email())
                    .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

            LoginResponse.TeamDto teamDto = new LoginResponse.TeamDto(
                    user.getTeam().getId(),
                    user.getTeam().getName());

            LoginResponse.UserDto userDto = new LoginResponse.UserDto(
                    user.getId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getRole().name().toLowerCase(),
                    teamDto);

            return new LoginResponse(userDto, token, jwtExpirationMs / 1000);
        } catch (AuthenticationException exception) {
            throw new UnauthorizedException("Invalid credentials");
        }
    }

    @Override
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.email());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String verificationCode = generateSixDigitCode();

            PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                    .user(user)
                    .token(verificationCode)
                    .expiresAt(Instant.now().plusSeconds(resetCodeExpirationSeconds))
                    .isUsed(false)
                    .createdAt(Instant.now())
                    .build();
            passwordResetTokenRepository.save(passwordResetToken);
        }

        return new ForgotPasswordResponse(resetCodeExpirationSeconds);
    }

    @Override
    public VerifyCodeResponse verifyCode(VerifyCodeRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestException("Invalid verification code"));

        PasswordResetToken passwordResetToken = passwordResetTokenRepository
                .findTopByUserEmailAndTokenAndIsUsedFalseOrderByCreatedAtDesc(request.email(), request.code())
                .orElseThrow(() -> new BadRequestException("Invalid verification code"));

        if (passwordResetToken.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("Verification code has expired");
        }

        passwordResetToken.setUsed(true);
        passwordResetTokenRepository.save(passwordResetToken);

        String resetToken = jwtService.generateResetToken(user.getEmail());
        return new VerifyCodeResponse(resetToken);
    }

    @Override
    public SimpleMessageResponse resetPassword(ResetPasswordRequest request) {
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new BadRequestException("Password and confirmPassword do not match");
        }

        String email;
        try {
            email = jwtService.extractUsername(request.resetToken());
        } catch (Exception exception) {
            throw new BadRequestException("Invalid reset token");
        }

        if (!jwtService.isResetTokenValid(request.resetToken(), email)) {
            throw new BadRequestException("Invalid or expired reset token");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Invalid reset token"));

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        return new SimpleMessageResponse("Şifrəniz uğurla yeniləndi");
    }

    private String generateSixDigitCode() {
        int value = new Random().nextInt(900000) + 100000;
        return String.valueOf(value);
    }
}
