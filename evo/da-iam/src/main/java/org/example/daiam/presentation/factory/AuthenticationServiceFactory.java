package org.example.daiam.presentation.factory;



import org.example.daiam.application.service.impl.DefaultAuthenticationServiceImpl;
import org.example.daiam.application.service.AuthenticationService;
import org.example.daiam.application.service.impl.KeycloakAuthenticationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor

public class AuthenticationServiceFactory {
    @Value("${application.authProvider}")
    String authProvider ;
    private final DefaultAuthenticationServiceImpl authenticationService;
    private final KeycloakAuthenticationServiceImpl keycloakAuthenticationService;
    public AuthenticationService getService() {
        return switch (authProvider) {
            case "DEFAULT" -> authenticationService;
            case "KEYCLOAK" -> keycloakAuthenticationService;
            default -> throw new IllegalArgumentException("Invalid service type: " + authProvider);
        };
    }
}
