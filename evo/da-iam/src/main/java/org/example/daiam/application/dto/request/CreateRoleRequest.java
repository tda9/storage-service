package org.example.daiam.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.Set;


public record CreateRoleRequest(
        @NotBlank(message = "RoleEntity name can not be empty")
        @Pattern(regexp = "^[a-zA-Z0-9_-]{3,}$", message = "RoleEntity name must contain letters or digits with minimum 3 character")
        String name,
        @NotEmpty(message = "Resource name can not be empty")
        Set<String> permissionsResourceName
){
}
