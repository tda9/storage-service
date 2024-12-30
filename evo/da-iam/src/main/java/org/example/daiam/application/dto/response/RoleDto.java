package org.example.daiam.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
public class RoleDto {
    private String roleId;
    private List<UUID> permissionIds;
}
