package org.example.daiam.dto.request;

import org.example.daiam.annotation.ValidScope;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.example.daiam.entity.Scope;

public record UpdatePermissionRequest(
        @NotEmpty(message = "Permission id can not be empty")
        String permissionId,
        @NotNull(message = "Resource name can not be null")
        String resourceName,
        @NotNull(message = "Scope can not be null")
        @ValidScope
        Scope scope,
        @NotNull(message = "Resource code can not be null")
        String resourceCode,
        @NotNull(message = "Deleted can not be null")
        Boolean deleted
) {
}
