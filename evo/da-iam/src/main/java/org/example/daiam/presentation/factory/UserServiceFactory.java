package org.example.daiam.presentation.factory;


import org.example.daiam.service.BaseUserService;
import org.example.daiam.service.impl.KeycloakUserService;
import org.example.daiam.service.impl.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserServiceFactory {
    @Value("${application.authProvider}")
    String authProvider ;
    private final UserService userService;
    private final KeycloakUserService keycloakUserService;
    public BaseUserService getUserService() {
        return switch (authProvider) {
            case "DEFAULT" -> userService;
            case "KEYCLOAK" -> keycloakUserService;
            default -> throw new IllegalArgumentException("Invalid service type: " + authProvider);
        };
    }
}
