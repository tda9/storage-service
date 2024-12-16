package org.example.daiam.controller;

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
import org.example.daiam.utils.InputUtils;
import org.example.daiam.utils.RSAKeyUtil;
import org.example.model.UserAuthority;
import org.example.model.dto.response.BasedResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final PasswordService passwordService;
    private final AuthorityServiceImpl authorityServiceImpl;
    private final AuthenticationServiceFactory authenticationServiceFactory;

    @GetMapping("/verify-email")
    public BasedResponse<?> verifyEmail(
            @RequestParam @Pattern(regexp = InputUtils.EMAIL_FORMAT, message = "Invalid verify email") String email,
            @RequestParam @NotBlank(message = "Missing email verification token") String token) {
            passwordService.verifyEmail(email, token);
            return BasedResponse.success("Verify email successful", email);
    }

    @PostMapping("/register")
    public BasedResponse<?> register(@RequestBody @Valid RegisterRequest request) {
        return BasedResponse.success("Register successful",
                authenticationServiceFactory.getService().register(request));
    }

    @PostMapping("/login")
    public BasedResponse<?> login(@RequestBody @Valid LoginRequest request) {
        return BasedResponse.success("Login successful",
                authenticationServiceFactory.getService().login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody @Valid LogoutRequest request) {
        return authenticationServiceFactory.getService().logout(request);
    }


    @PostMapping("/refresh-token")
    public BasedResponse<?> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        return BasedResponse.success("Refresh token successful",
                authenticationServiceFactory.getService().refreshToken(request.refreshToken()));
    }

    @PreAuthorize("hasPermission('USERS','UPDATE')")
    @PostMapping("/change-password")
    public BasedResponse<?> changePassword(
            @RequestBody ChangePasswordRequest request) {
        authenticationServiceFactory.getService().changePassword(request);
        return BasedResponse.success("Change password successful", request.email());
    }

    @PostMapping("/forgot-password")
    public BasedResponse<?> forgotPassword(
            @RequestParam @NotEmpty(message = "Missing forgot email")
            @Pattern(regexp = InputUtils.EMAIL_FORMAT, message = "Invalid verify email")String email) {
            passwordService.forgotPassword(email);
            return BasedResponse.success("If your email existed, you will receive an email", email);
    }

    @GetMapping("/reset-password")
    public BasedResponse<?> resetPassword(@RequestParam String email, @RequestParam String newPassword, @RequestParam String token) {
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
    public ResponseEntity<?> getClientToken(@PathVariable String clientId, @PathVariable String clientSecret) {
        return ResponseEntity.ok(authenticationServiceFactory.getService()
                .getClientToken(clientId, clientSecret));
    }
}
