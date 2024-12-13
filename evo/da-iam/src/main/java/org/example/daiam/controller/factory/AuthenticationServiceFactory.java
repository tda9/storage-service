package org.example.daiam.controller.factory;



import org.example.daiam.service.impl.AuthenticationService;
import org.example.daiam.service.BaseAuthenticationService;
import org.example.daiam.service.impl.KeycloakAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor

public class AuthenticationServiceFactory {
    @Value("${application.authProvider}")
    String authProvider ;
    private final AuthenticationService authenticationService;
    private final KeycloakAuthenticationService keycloakAuthenticationService;
    public BaseAuthenticationService getService() {
        return switch (authProvider) {
            case "DEFAULT" -> authenticationService;
            case "KEYCLOAK" -> keycloakAuthenticationService;
            default -> throw new IllegalArgumentException("Invalid service type: " + authProvider);
        };
    }
}
