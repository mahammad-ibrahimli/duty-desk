package com.example.dutydesk.service;

import com.example.dutydesk.dto.request.auth.LoginRequest;
import com.example.dutydesk.dto.request.auth.ForgotPasswordRequest;
import com.example.dutydesk.dto.request.auth.ResetPasswordRequest;
import com.example.dutydesk.dto.request.auth.VerifyCodeRequest;
import com.example.dutydesk.dto.response.auth.ForgotPasswordResponse;
import com.example.dutydesk.dto.response.auth.LoginResponse;
import com.example.dutydesk.dto.response.auth.SimpleMessageResponse;
import com.example.dutydesk.dto.response.auth.VerifyCodeResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request);

    VerifyCodeResponse verifyCode(VerifyCodeRequest request);

    SimpleMessageResponse resetPassword(ResetPasswordRequest request);
}
