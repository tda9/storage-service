package org.example.daiam.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.daiam.dto.request.ChangePasswordRequest;
import org.example.daiam.dto.request.LoginRequest;
import org.example.daiam.dto.request.LogoutRequest;
import org.example.daiam.dto.request.RegisterRequest;
import org.example.model.dto.response.BaseTokenResponse;
import org.example.daiam.entity.User;
import org.springframework.http.ResponseEntity;

import java.util.UUID;


public interface  BaseAuthenticationService {
    User register(RegisterRequest request);

    BaseTokenResponse login(LoginRequest loginRequest, HttpServletRequest servletRequest);

    BaseTokenResponse refreshToken(String refreshToken, HttpServletRequest servletRequest);

    ResponseEntity<?> logout(LogoutRequest request,HttpServletRequest servletRequest);

    void resetPassword(String email, String newPassword, String token);

    void changePassword(ChangePasswordRequest request);

    BaseTokenResponse getClientToken(UUID clientId, String clientSecret);
}
