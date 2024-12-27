package org.example.daiam.application.service.factory;


import lombok.RequiredArgsConstructor;
import org.example.daiam.application.service.UserCommandService;
import org.example.daiam.application.service.impl.DefaultUserCommandServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCommandServiceFactory {
    @Value("${application.authProvider}")
    String authProvider ;
    private final DefaultUserCommandServiceImpl userService;

    public UserCommandService getUserService() {
        return switch (authProvider) {
            case "DEFAULT" -> userService;
            default -> throw new IllegalArgumentException("Invalid service type: " + authProvider);
        };
    }
}
