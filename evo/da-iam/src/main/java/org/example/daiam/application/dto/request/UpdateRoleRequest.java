package org.example.daiam.application.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record UpdateRoleRequest (
        @NotEmpty(message = "RoleEntity id can not be empty")
        String roleId,
        @NotNull(message = "RoleEntity name can not be empty")
        String name,
        @NotEmpty(message = "RoleEntity permission can not be empty")
        Set<String> permissionsResourceName,
        @NotNull(message = "RoleEntity deleted can not be null")
        Boolean deleted
){
}
