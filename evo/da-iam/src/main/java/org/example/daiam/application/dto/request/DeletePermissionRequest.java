package org.example.daiam.application.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record DeletePermissionRequest(
        @NotEmpty(message = "PermissionEntity id can not be empty")
        String permissionId
) {

}
