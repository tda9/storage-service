package org.example.daiam.service;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import org.example.daiam.dto.Credentials;
import org.example.daiam.dto.request.UpdateUserRequest;
import org.example.daiam.repo.BlackListTokenRepo;
import org.example.daiam.repo.RoleRepo;
import org.example.daiam.repo.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.example.web.support.RedisService;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;
import java.util.List;

@Slf4j
public class BaseKeycloakService extends BaseService {
    @Value("${application.security.keycloak.serverUrl}")
    private String serverUrl;
    @Value("${application.security.keycloak.realm}")
    private String realm;
    @Value("${application.security.keycloak.clientId}")
    private String clientId;
    @Value("${application.security.keycloak.clientSecret}")
    private String clientSecret;
    @Value("${application.security.keycloak.grantType}")
    private String grantType;
    @Value("${application.security.keycloak.username}")
    private String username;
    @Value("${application.security.keycloak.password}")
    private String password;
    @Value("${application.security.keycloak.logoutUrl}")
    private String LOGOUT_URL;
    @Value("${application.security.keycloak.newAccessTokenUrl}")
    private String NEW_ACCESS_TOKEN_URL;
    private final Keycloak keycloak;

    public BaseKeycloakService(Keycloak keycloak,
                               UserRepo userRepo,
                               RoleRepo roleRepo,
                               JWTService jwtService,
                               RedisService redisService
    ) {
        super(userRepo, roleRepo, jwtService,redisService);
        this.keycloak = keycloak;
    }

    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                //.clientId(clientId)
                // .clientSecret(clientSecret)
                //.grantType(grantType)
                .authorization("Bearer " + keycloak.tokenManager().getAccessTokenString())
                //.username(username)
                //.password(password)
                .build();
    }

//    public String getAccessTokenMaster() {
//        return keycloak.tokenManager().getAccessTokenString();
//    }

    public void updateKeycloakUser(UpdateUserRequest request, String oldEmail) {
        List<UserRepresentation> users = getUsersResource().searchByEmail(oldEmail, true);//Use searchByEmail to find exact email to find the user
        if (users.isEmpty()) {
            throw new NotFoundException("User with email " + oldEmail + " not found.");
        }
        UserRepresentation userRepresentation = users.getFirst();
        userRepresentation.setEnabled(!request.isLock());
        userRepresentation.setEmail(request.email());
        getUsersResource().get(userRepresentation.getId()).update(userRepresentation);
    }

    protected void createKeycloakUser(String email, String password) {
        CredentialRepresentation credential = Credentials.createPasswordCredentials(password);
        UserRepresentation user = new UserRepresentation();
        user.setUsername(email);
        user.setEmail(email);
        user.setCredentials(Collections.singletonList(credential));
        user.setEnabled(true);
        getUsersResource().create(user);
    }

    protected UsersResource getUsersResource() {
        return keycloak.realm(realm).users();
    }
}
