package org.example.daiam.application.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.example.daiam.annotation.ValidScope;
import org.example.daiam.infrastruture.support.Scope;


public record UpdatePermissionRequest(
        String resourceName,
        @ValidScope
        Scope scope,
        String resourceCode,
        Boolean deleted) {
}
