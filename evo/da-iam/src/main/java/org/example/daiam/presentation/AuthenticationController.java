package org.example.daiam.presentation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.example.daiam.application.dto.request.*;
import org.example.daiam.presentation.factory.AuthenticationServiceFactory;


import org.example.daiam.application.service.others.PasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.web.support.MessageUtils;
import org.example.daiam.utils.RSAKeyUtil;
import org.example.model.dto.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class AuthenticationController {
    private final PasswordService passwordService;
    private final AuthenticationServiceFactory authenticationServiceFactory;

    @PostMapping("/auth/register")
    public Response<?> register(@RequestBody @Valid RegisterRequest request) {
        return Response.success("Register successful",
                authenticationServiceFactory.getService().register(request));
    }

    @PostMapping("/auth/login")
    public Response<?> login(@RequestBody @Valid LoginRequest request, HttpServletRequest servletRequest) {
        return Response.success("Login successful",
                authenticationServiceFactory.getService().login(request, servletRequest));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(@RequestBody @Valid LogoutRequest request, HttpServletRequest servletRequest) {
        return authenticationServiceFactory.getService().logout(request, servletRequest);
    }

    @PostMapping("/auth/token/refresh")
    public Response<?> refresh(
            @RequestBody @Valid RefreshTokenRequest request,
            HttpServletRequest servletRequest) {
        return Response.success("Refresh token successful",
                authenticationServiceFactory.getService().refreshToken(request.refreshToken(), servletRequest));
    }

    @PreAuthorize("hasPermission(null,'user.update')")
    @PostMapping("/auth/password/change")
    public Response<?> change(
            @RequestBody @Valid ChangePasswordRequest request) {
        authenticationServiceFactory.getService().changePassword(request);
        return Response.success("Change password successful", request.email());
    }

    @GetMapping("/auth/password/reset")
    public Response<?> reset(
            @RequestParam @NotBlank @Pattern(regexp = MessageUtils.EMAIL_FORMAT) String email,
            @RequestParam @NotBlank @Pattern(regexp = MessageUtils.PASSWORD_PATTERN) String newPassword,
            @RequestParam @NotBlank String token) {
        authenticationServiceFactory.getService().resetPassword(email, newPassword, token);
        return Response.success("Reset password successful", email);
    }

    @PostMapping("/auth/password/forgot")
    public Response<?> forgot(
            @RequestParam @NotEmpty(message = "Missing forgot email")
            @Pattern(regexp = MessageUtils.EMAIL_FORMAT, message = "Invalid verify email") String email) {
        passwordService.forgotPassword(email);
        return Response.success("If your email existed, you will receive an email", email);
    }

    private final RSAKeyUtil rsaKeyUtil;

    @GetMapping("/auth/client/token/{clientId}/{clientSecret}")
    public ResponseEntity<?> getClientToken(
            @PathVariable UUID clientId,
            @PathVariable @NotBlank String clientSecret) {
        return ResponseEntity.ok(authenticationServiceFactory.getService()
                .getClientToken(clientId, clientSecret));
    }
    @GetMapping("/auth/email/verify")
    public Response<?> verify(
            @RequestParam @Pattern(regexp = MessageUtils.EMAIL_FORMAT,
                    message = MessageUtils.INVALID_EMAIL_PATTERN_MESSAGE) String email,
            @RequestParam @NotBlank(message = "Missing verification token") String token) {
        passwordService.verifyEmail(email, token);
        return Response.success("Verify email successful", email);
    }

    @GetMapping("/custom-login")
    public Response<?> redirectToKeycloakLogin() {
        return Response.builder()
                .requestStatus(true)
                .message("Please redirects to the Keycloak login page")
                .data("http://localhost:8082")
                .httpStatusCode(301)
                .build();
    }

    @GetMapping("/auth/certificate/.well-known/jwks.json")
    public ResponseEntity<?> getPublicKeyIam() {
        return ResponseEntity.ok(rsaKeyUtil.jwkSet().toJSONObject());
    }
}
