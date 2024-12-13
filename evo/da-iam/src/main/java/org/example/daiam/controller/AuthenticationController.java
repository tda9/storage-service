package org.example.daiam.controller;

import org.example.daiam.controller.factory.AuthenticationServiceFactory;
import org.example.daiam.dto.request.*;

import org.example.daiam.dto.response.DefaultClientTokenResponse;
import org.example.daiam.exception.ErrorResponseException;
import org.example.daiam.service.PasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.daiam.service.impl.AuthorityServiceImpl;
import org.example.daiam.utils.RSAKeyUtil;
import org.example.model.UserAuthority;
import org.example.model.dto.response.BasedResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class AuthenticationController {
    private final PasswordService passwordService;
    private final AuthorityServiceImpl authorityService;
    private final AuthenticationServiceFactory authenticationServiceFactory;

    @GetMapping("/confirmation-registration")
    public BasedResponse<?> confirmRegister(@RequestParam String email, @RequestParam String token) {
        try {
            passwordService.confirmRegisterEmail(email, token);
            return BasedResponse.success("Confirm successful", email);
        } catch (Exception e) {
            throw new ErrorResponseException(e.getMessage());
        }
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

    @PostMapping("/api/logout")
    public String logout(@RequestBody LogoutRequest request) {
        authenticationServiceFactory.getService().logout(request);
        return "Logout request has been sent.";
    }

    @PostMapping("/refresh-token")
    public BasedResponse<?> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        return BasedResponse.success("Refresh token successful",
                authenticationServiceFactory.getService().refreshToken(request.refreshToken()));
    }

    @PostMapping("/change-password")
    public BasedResponse<?> changePassword(
            @RequestBody ChangePasswordRequest request) {
        authenticationServiceFactory.getService().changePassword(
                request.currentPassword(),
                request.newPassword(),
                request.confirmPassword(),
                request.email());
        return BasedResponse.builder()
                .httpStatusCode(200)
                .requestStatus(true)
                .message("Change password successful")
                .data(request.email())
                .build();
    }

    @PostMapping("/forgot-password")
    public BasedResponse<?> forgotPassword(@RequestParam String email) {
        try {
            passwordService.forgotPassword(email);
            return BasedResponse.success("If your email existed, you will receive a link", email);
        } catch (Exception e) {
            throw new ErrorResponseException("Error forgot password");
        }
    }

    @GetMapping("/reset-password")
    public BasedResponse<?> resetPassword(@RequestParam String email, @RequestParam String newPassword, @RequestParam String token) {
        authenticationServiceFactory.getService().resetPassword(email, newPassword, token);
        return BasedResponse.success("Reset password successful", email);
    }

    @PreAuthorize("hasPermission('HOMEPAGE','VIEW')")
    @GetMapping("/hello")
    public String test() {
        return "Hello HOMEPAGE";
    }

    @PreAuthorize("hasPermission('DASHBOARD','VIEW')")
    @GetMapping("/admin")
    public String test1() {
        return "Hello DASHBOARD ";
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

    @GetMapping("/iam/certificate/.well-known/public.pem")
    public ResponseEntity<?> getPublicKeyIam() {
        return ResponseEntity.ok(rsaKeyUtil.jwkSet().toJSONObject());
    }

    @GetMapping("/iam/client-token/{clientId}/{clientSecret}")
    public ResponseEntity<?> getClientToken(@PathVariable String clientId, @PathVariable String clientSecret) throws Exception {
        return ResponseEntity.ok(authenticationServiceFactory.getService().getClientToken(DefaultClientTokenResponse.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build()));
    }

    @GetMapping("/api/users/{username}/authorities-by-username")
    BasedResponse<UserAuthority> getUserAuthority(@PathVariable String username) {
        return BasedResponse.success("Get authorities successful for " + username, authorityService.getUserAuthority(username));
    }

}
