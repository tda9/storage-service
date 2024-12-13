package org.example.web.config;

import org.example.model.UserAuthentication;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.regex.Pattern;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        String requiredPermission = targetDomainObject.toString()+"."+permission.toString();
        if (!(authentication instanceof UserAuthentication userAuthentication)) {
            // @TODO throw exception
            throw new RuntimeException("NOT_SUPPORTED_AUTHENTICATION");
        }
        if (userAuthentication.isRoot()) {
            return true;
        }
        return userAuthentication.getGrantedPermissions().stream()
                .anyMatch(p -> Pattern.matches(p, requiredPermission));
    }
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return hasPermission(authentication, null, permission);
    }
}
