package org.example.daiam.presentation.factory;



import org.example.daiam.application.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component
public class AuthenticationServiceFactory {

    @Value("${application.authProvider}")
    private String authProvider;

    private final AuthenticationService defaultAuthenticationService;
    private final AuthenticationService keycloakAuthenticationService;

    public AuthenticationServiceFactory(
            @Qualifier("defaultAuthenticationServiceImpl") AuthenticationService defaultAuthenticationService,
            @Qualifier("keycloakAuthenticationServiceImpl") AuthenticationService keycloakAuthenticationService) {
        this.defaultAuthenticationService = defaultAuthenticationService;
        this.keycloakAuthenticationService = keycloakAuthenticationService;
    }

    public AuthenticationService getService() {
        // Return the correct service based on the authProvider
        return switch (authProvider) {
            case "DEFAULT" -> defaultAuthenticationService;
            case "KEYCLOAK" -> keycloakAuthenticationService;
            default -> throw new IllegalArgumentException("Invalid service type: " + authProvider);
        };
    }
}
