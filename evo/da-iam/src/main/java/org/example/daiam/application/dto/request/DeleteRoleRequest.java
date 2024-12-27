package org.example.daiam.application.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record DeleteRoleRequest(
        @NotEmpty(message = "ROLE_ID_REQUIRED")
        String roleId) {
}
