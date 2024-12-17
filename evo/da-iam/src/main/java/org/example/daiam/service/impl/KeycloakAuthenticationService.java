package org.example.daiam.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.example.daiam.service.KeycloakClient;
import org.example.daiam.dto.request.ChangePasswordRequest;
import org.example.daiam.dto.request.LoginRequest;
import org.example.daiam.dto.request.LogoutRequest;
import org.example.daiam.dto.request.RegisterRequest;
import org.example.model.dto.response.BaseTokenResponse;

import org.example.daiam.dto.response.KeycloakAccessTokenResponse;


import org.example.daiam.entity.User;

import lombok.extern.slf4j.Slf4j;
import org.example.daiam.repo.BlackListTokenRepo;
import org.example.daiam.repo.RoleRepo;
import org.example.daiam.repo.UserRepo;
import org.example.daiam.service.*;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;


@Service
@Slf4j
public class KeycloakAuthenticationService extends BaseKeycloakService implements BaseAuthenticationService {
    private final PasswordService passwordService;
    private final AuthenticationService authenticationService;
    private final KeycloakClient keycloakClient;
    @Value("${application.security.keycloak.realm}")
    private String realm;
    @Value("${application.security.keycloak.clientId}")
    private String clientId;
    @Value("${application.security.keycloak.clientSecret}")
    private String clientSecret;
    @Value("${application.security.keycloak.serverUrl}")
    private String serverUrl;
    @Value("${application.security.keycloak.logoutUrl}")
    private String logoutUrl;
    @Value("${application.security.keycloak.newAccessTokenUrl}")
    private String newAccessTokenUrl;

    public KeycloakAuthenticationService(UserRepo userRepo,
                                         RoleRepo roleRepo,
                                         Keycloak keycloak,
                                         PasswordService passwordService,
                                         AuthenticationService authenticationService,
                                         BlackListTokenRepo blackListTokenRepo,
                                         JWTService jwtService,
                                         KeycloakClient keycloakClient
    ) {
        super(keycloak, userRepo, roleRepo, blackListTokenRepo, jwtService);
        this.passwordService = passwordService;
        this.authenticationService = authenticationService;
        this.keycloakClient = keycloakClient;
    }

    @Override
    public User register(RegisterRequest request) {
        User user = authenticationService.register(request);
        createKeycloakUser(request.email(), request.password());
        return user;
    }

    @Override
    public BaseTokenResponse login(LoginRequest request, HttpServletRequest servletRequest) {
        authenticationService.login(request,servletRequest);
        return getKeycloakUserToken(request.email(), request.password());
    }

    @Override
    public BaseTokenResponse getClientToken(UUID clientId, String clientSecret) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("grant_type", "client_credentials");
        return keycloakClient.getKeycloakClientToken(body).getBody();
    }


    @Override
    public ResponseEntity<?> logout(LogoutRequest request, HttpServletRequest servletRequest) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", request.refreshToken());
        return keycloakClient.logout(body);
    }


    @Override
    public BaseTokenResponse refreshToken(String refreshToken) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", refreshToken);
        body.add("grant_type", "refresh_token");
        return keycloakClient.refreshToken(body).getBody();
    }

    public KeycloakAccessTokenResponse getKeycloakUserToken(String username, String password) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("username", username);
        body.add("password", password);
        return keycloakClient.getKeycloakUserToken(body).getBody();
    }

    @Override
    public void resetPassword(String email, String newPassword, String token) {
        passwordService.resetPassword(email, newPassword, token);
        UsersResource usersResource = getUsersResource();
        List<UserRepresentation> users = usersResource.searchByEmail(email, true);// Use searchByEmail to find the user
        resetKeycloakUserPassword(users, newPassword);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        passwordService.changePassword(request);
        List<UserRepresentation> users = getUsersResource().searchByEmail(request.email(), true);
        resetKeycloakUserPassword(users, request.newPassword());
    }

    private void resetKeycloakUserPassword(List<UserRepresentation> users, String newPassword) {
        try {
            if (users.isEmpty()) {
                throw new NotFoundException("User not found in keycloak");
            }
            UserRepresentation userRepresentation = users.getFirst();
            String userId = userRepresentation.getId();
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(newPassword);
            credential.setTemporary(false);
            UserResource userResource = getUsersResource().get(userId);
            userResource.resetPassword(credential);
        }catch (Exception e){
            throw new BadRequestException("Reset password failed", e);
        }
    }

}
