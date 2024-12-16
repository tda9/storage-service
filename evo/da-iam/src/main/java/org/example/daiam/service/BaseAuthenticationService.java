package org.example.daiam.service;

import org.example.daiam.dto.request.ChangePasswordRequest;
import org.example.daiam.dto.request.LoginRequest;
import org.example.daiam.dto.request.LogoutRequest;
import org.example.daiam.dto.request.RegisterRequest;
import org.example.daiam.dto.response.BaseTokenResponse;
import org.example.daiam.dto.response.DefaultClientTokenResponse;
import org.example.daiam.entity.User;
import org.springframework.http.ResponseEntity;


public interface  BaseAuthenticationService {
    User register(RegisterRequest request);

    BaseTokenResponse login(LoginRequest loginRequest);

    BaseTokenResponse refreshToken(String refreshToken);

    ResponseEntity<?> logout(LogoutRequest request);

    void resetPassword(String email, String newPassword, String token);

    void changePassword(ChangePasswordRequest request);

    BaseTokenResponse getClientToken(String clientId, String clientSecret);
}
