package org.example.daiam.application.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.daiam.application.dto.request.ChangePasswordRequest;
import org.example.daiam.application.dto.request.LoginRequest;
import org.example.daiam.application.dto.request.LogoutRequest;
import org.example.daiam.application.dto.request.RegisterRequest;
import org.example.daiam.domain.User;

import org.example.model.dto.response.AbstractTokens;

import org.springframework.http.ResponseEntity;

import java.util.UUID;


public interface AuthenticationService {
    User register(RegisterRequest request);

    AbstractTokens login(LoginRequest loginRequest, HttpServletRequest servletRequest);

    AbstractTokens refreshToken(String refreshToken, HttpServletRequest servletRequest);

    ResponseEntity<?> logout(LogoutRequest request, HttpServletRequest servletRequest);

    void resetPassword(String email, String newPassword, String token);

    void changePassword(ChangePasswordRequest request);

    AbstractTokens getClientToken(UUID clientId, String clientSecret);
}
