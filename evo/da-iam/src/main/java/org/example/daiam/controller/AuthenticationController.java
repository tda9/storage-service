package org.example.daiam.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.example.daiam.controller.factory.AuthenticationServiceFactory;
import org.example.daiam.dto.request.*;

import org.example.daiam.dto.response.DefaultClientTokenResponse;
import org.example.daiam.service.PasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.daiam.service.impl.AuthorityServiceImpl;
import org.example.daiam.service.impl.TokenServiceImpl;
import org.example.daiam.utils.InputUtils;
import org.example.daiam.utils.RSAKeyUtil;
import org.example.model.UserAuthority;
import org.example.model.dto.response.BasedResponse;
import org.example.web.security.impl.TokenCacheServiceImpl;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final PasswordService passwordService;
    private final AuthenticationServiceFactory authenticationServiceFactory;

    @PostMapping("/register")
    public BasedResponse<?> register(@RequestBody @Valid RegisterRequest request) {
        return BasedResponse.success("Register successful",
                authenticationServiceFactory.getService().register(request));
    }

    @PostMapping("/login")
    public BasedResponse<?> login(@RequestBody @Valid LoginRequest request, HttpServletRequest servletRequest) {
        return BasedResponse.success("Login successful",
                authenticationServiceFactory.getService().login(request, servletRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody @Valid LogoutRequest request, HttpServletRequest servletRequest) {
        return authenticationServiceFactory.getService().logout(request, servletRequest);
    }


    @PostMapping("/refresh-token")
    public BasedResponse<?> refreshToken(
            @RequestBody @Valid RefreshTokenRequest request,
            HttpServletRequest servletRequest) {
        return BasedResponse.success("Refresh token successful",
                authenticationServiceFactory.getService().refreshToken(request.refreshToken(), servletRequest));
    }

    @PreAuthorize("hasPermission('USERS','UPDATE')")
    @PostMapping("/change-password")
    public BasedResponse<?> changePassword(
            @RequestBody @Valid ChangePasswordRequest request) {
        authenticationServiceFactory.getService().changePassword(request);
        return BasedResponse.success("Change password successful", request.email());
    }

    @GetMapping("/reset-password")
    public BasedResponse<?> resetPassword(
            @RequestParam @NotBlank @Pattern(regexp = InputUtils.EMAIL_FORMAT) String email,
            @RequestParam @NotBlank @Pattern(regexp = InputUtils.PASSWORD_FORMAT) String newPassword,
            @RequestParam @NotBlank String token) {
        authenticationServiceFactory.getService().resetPassword(email, newPassword, token);
        return BasedResponse.success("Reset password successful", email);
    }

    @GetMapping("/custom-login")
    public BasedResponse<?> redirectToKeycloakLogin() {
        return BasedResponse.builder()
                .requestStatus(true)
                .message("Please redirects to the Keycloak login page")
                .data("http://localhost:8082")
                .httpStatusCode(301)
                .build();
    }

    private final RSAKeyUtil rsaKeyUtil;

    @GetMapping("/certificate/.well-known/jwks.json")
    public ResponseEntity<?> getPublicKeyIam() {
        return ResponseEntity.ok(rsaKeyUtil.jwkSet().toJSONObject());
    }

    @GetMapping("/client-token/{clientId}/{clientSecret}")
    public ResponseEntity<?> getClientToken(
            @PathVariable UUID clientId,
            @PathVariable @NotBlank String clientSecret) {
        return ResponseEntity.ok(authenticationServiceFactory.getService()
                .getClientToken(clientId, clientSecret));
    }
    @GetMapping("/verify-email")
    public BasedResponse<?> verifyEmail(
            @RequestParam @Pattern(regexp = InputUtils.EMAIL_FORMAT, message = "Invalid verify email") String email,
            @RequestParam @NotBlank(message = "Missing email verification token") String token) {
        passwordService.verifyEmail(email, token);
        return BasedResponse.success("Verify email successful", email);
    }

    @PostMapping("/forgot-password")
    public BasedResponse<?> forgotPassword(
            @RequestParam @NotEmpty(message = "Missing forgot email")
            @Pattern(regexp = InputUtils.EMAIL_FORMAT, message = "Invalid verify email") String email) {
        passwordService.forgotPassword(email);
        return BasedResponse.success("If your email existed, you will receive an email", email);
    }
}
