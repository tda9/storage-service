//package org.example.daiam.service;
//
//import jakarta.ws.rs.NotFoundException;
//import jakarta.ws.rs.core.Response;
//import org.example.daiam.application.dto.KeycloakCredentials;
//import org.example.daiam.dto.request.UpdateUserRequest;
//
//import lombok.extern.slf4j.Slf4j;
//import org.example.daiam.infrastruture.persistence.repository.RoleEntityRepository;
//import org.example.daiam.infrastruture.persistence.repository.UserEntityRepository;
//import org.example.web.support.RedisService;
//import org.keycloak.admin.client.Keycloak;
//import org.keycloak.admin.client.KeycloakBuilder;
//import org.keycloak.admin.client.resource.UsersResource;
//import org.keycloak.representations.idm.CredentialRepresentation;
//import org.keycloak.representations.idm.UserRepresentation;
//import org.springframework.beans.factory.annotation.Value;
//
//import java.util.Collections;
//import java.util.List;
//
//@Slf4j
//public class BaseKeycloakService extends CommonService {
//    @Value("${application.security.keycloak.serverUrl}")
//    private String serverUrl;
//    @Value("${application.security.keycloak.realm}")
//    private String realm;
//    @Value("${application.security.keycloak.clientId}")
//    private String clientId;
//    @Value("${application.security.keycloak.clientSecret}")
//    private String clientSecret;
//    @Value("${application.security.keycloak.grantType}")
//    private String grantType;
//    @Value("${application.security.keycloak.username}")
//    private String username;
//    @Value("${application.security.keycloak.password}")
//    private String password;
//    @Value("${application.security.keycloak.logoutUrl}")
//    private String LOGOUT_URL;
//    @Value("${application.security.keycloak.newAccessTokenUrl}")
//    private String NEW_ACCESS_TOKEN_URL;
//    private final Keycloak keycloak;
//
//
//
//    public Keycloak keycloak() {
//        return KeycloakBuilder.builder()
//                .serverUrl(serverUrl)
//                .realm(realm)
//                //.clientId(clientId)
//                // .clientSecret(clientSecret)
//                //.grantType(grantType)
//                .authorization("Bearer " + keycloak.tokenManager().getAccessTokenString())
//                //.username(username)
//                //.password(password)
//                .build();
//    }
//
////    public String getAccessTokenMaster() {
////        return keycloak.tokenManager().getAccessTokenString();
////    }
//
//    public void updateKeycloakUser(UpdateUserRequest request, String oldEmail) {
//        List<UserRepresentation> users = getUsersResource().searchByEmail(oldEmail, true);//Use searchByEmail to find exact email to find the user
//        if (users.isEmpty()) {
//            throw new NotFoundException("User with email " + oldEmail + " not found.");
//        }
//        UserRepresentation userRepresentation = users.getFirst();
//        userRepresentation.setEnabled(!request.isLock());
//        userRepresentation.setEmail(request.email());
//        getUsersResource().get(userRepresentation.getId()).update(userRepresentation);
//    }
//
//    protected void createKeycloakUser(String email, String password) {
//        try {
//            // Create password credential
//            CredentialRepresentation credential = KeycloakCredentials.createPasswordCredentials(password);
//
//            // Create user representation
//            UserRepresentation user = new UserRepresentation();
//            user.setUsername(email);
//            user.setEmail(email);
//            user.setCredentials(Collections.singletonList(credential));
//            user.setEnabled(true);
//
//            // Call Keycloak API to create user
//            Response response = getUsersResource().create(user);
//
//            // Log status and response details
//            int status = response.getStatus();
//            System.out.println("Response Status: " + status);
//
//            if (status == 201) {
//                System.out.println("User created successfully: " + email);
//            } else {
//                String errorMessage = response.readEntity(String.class);
//                System.out.println("Error creating user: " + errorMessage);
//            }
//
//            response.close(); // Close the response to release resources
//        } catch (Exception e) {
//            System.out.println("Exception occurred while creating Keycloak user: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    protected UsersResource getUsersResource() {
//        return keycloak.realm(realm).users();
//    }
//}
