package org.example.daiam.application.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.example.daiam.application.dto.AccessTokens;
import org.example.daiam.application.dto.request.ChangePasswordRequest;
import org.example.daiam.application.dto.request.LoginRequest;
import org.example.daiam.application.dto.request.LogoutRequest;
import org.example.daiam.application.dto.request.RegisterRequest;
import org.example.daiam.application.service.KeycloakAbstractService;
import org.example.daiam.domain.User;

import lombok.extern.slf4j.Slf4j;
import org.example.daiam.application.service.AuthenticationService;
import org.example.daiam.application.service.others.CommonService;
import org.example.daiam.application.service.others.JwtService;
import org.example.daiam.application.service.others.PasswordService;
import org.example.model.dto.response.AbstractTokens;
import org.example.model.dto.response.ClientTokens;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;


@Service("keycloakAuthenticationServiceImpl")
@Slf4j
public class KeycloakAuthenticationServiceImpl
        extends KeycloakAbstractService
        implements AuthenticationService {
    private final PasswordService passwordService;
    private final AuthenticationService authenticationService;
    private final JwtService commonService;
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

    public KeycloakAuthenticationServiceImpl(Keycloak keycloak, PasswordService passwordService,
                                             AuthenticationService authenticationService,
                                             CommonService commonService,
                                             JwtService commonService1) {
        super(keycloak);
        this.passwordService = passwordService;
        this.authenticationService = authenticationService;
        this.commonService = commonService1;
    }

    @Override
    public User register(RegisterRequest request) {
        User user = authenticationService.register(request);
        createKeycloakUser(request.email(), request.password());
        return user;
    }

    @Override
    public AbstractTokens login(LoginRequest request, HttpServletRequest servletRequest) {
        authenticationService.login(request, servletRequest);
        return getKeycloakUserToken(request.email(), request.password());
    }

    //    @Override
//    public AbstractTokens getClientToken(UUID clientId, String clientSecret) {
//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//        body.add("client_id", clientId);
//        body.add("client_secret", clientSecret);
//        body.add("grant_type", "client_credentials");
//        return keycloakClient.getKeycloakClientToken(body).getBody();
//    }
    public AbstractTokens getClientToken(UUID clientId, String clientSecret) {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId.toString()); // Ensure clientId is converted to a String
        body.add("client_secret", clientSecret);
        body.add("grant_type", "client_credentials");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ;
        ResponseEntity<ClientTokens> response = restTemplate.exchange(
                serverUrl + newAccessTokenUrl,
                HttpMethod.POST,
                requestEntity,
                ClientTokens.class
        );
        // Return the token response body
        return response.getBody();
    }


    @Override
    public ResponseEntity<?> logout(LogoutRequest request, HttpServletRequest servletRequest) {
//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//        initBodyRequest(body);
//        body.add("refresh_token", request.refreshToken());
//        saveBlackAccessToken(servletRequest);
//        return keycloakClient.logout(body);
        commonService.saveBlackRefreshToken(servletRequest, request.refreshToken());
        String refreshToken = request.refreshToken();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", refreshToken);
        HttpEntity<MultiValueMap<String, String>> requestKeycloak = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                serverUrl + logoutUrl,
                HttpMethod.POST,
                requestKeycloak,
                String.class
        );
        // Check response status
        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Logout successful!");
        } else {
            System.out.println("Logout failed: " + response.getStatusCode());
        }
        return response;
    }


    @Override
    public AbstractTokens refreshToken(String refreshToken, HttpServletRequest servletRequest) {
//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//        initBodyRequest(body);
//        body.add("refresh_token", refreshToken);
//        body.add("grant_type", "refresh_token");
        commonService.saveBlackRefreshToken(servletRequest, refreshToken);
//        return keycloakClient.refreshToken(body).getBody();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", refreshToken);
        body.add("grant_type", "refresh_token");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<AccessTokens> response = restTemplate
                .exchange(serverUrl + newAccessTokenUrl,
                        HttpMethod.POST, request, AccessTokens.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Logout successful!");
        } else {
            System.out.println("Logout failed: " + response.getStatusCode());
        }
        return response.getBody();
    }


    private AccessTokens getKeycloakUserToken(String username, String password) {
//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//        body.add("grant_type", "password");
//        initBodyRequest(body);
//        body.add("username", username);
//        body.add("password", password);
//        return keycloakClient.getKeycloakUserToken(body).getBody();

        String tokenUrl = serverUrl + newAccessTokenUrl;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("username", username);
        body.add("password", password);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        //todo: throw exception for bad credential case here
        ResponseEntity<AccessTokens> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, AccessTokens.class);
        return response.getBody();

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
        } catch (Exception e) {
            throw new BadRequestException("Reset password failed", e);
        }
    }

}
