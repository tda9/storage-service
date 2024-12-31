package org.example.daiam.presentation.factory;


import org.example.daiam.application.service.UserCommandService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserServiceFactory {

    @Value("${application.authProvider}")
    private String authProvider;

    private final UserCommandService defaultUserService;
    private final UserCommandService keycloakUserService;

    public UserServiceFactory(
            @Qualifier("userCommandServiceImpl") UserCommandService defaultUserService,
            @Qualifier("keycloakUserCommandServiceImpl") UserCommandService keycloakUserService) {
        this.defaultUserService = defaultUserService;
        this.keycloakUserService = keycloakUserService;
    }

    public UserCommandService getUserService() {
        return switch (authProvider) {
            case "DEFAULT" -> defaultUserService;
            case "KEYCLOAK" -> keycloakUserService;
            default -> throw new IllegalArgumentException("Invalid service type: " + authProvider);
        };
    }
}
