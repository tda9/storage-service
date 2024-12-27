package org.example.daiam.domain;

import lombok.*;
import org.example.daiam.domain.command.CreateRoleCommand;

import java.util.*;


@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Role {
    private UUID roleId;
    private String name;
    private boolean deleted;
    List<RolePermission> rolePermissions = new ArrayList<>();

    public Role(CreateRoleCommand cmd){
        this.roleId = UUID.randomUUID();
        this.name = cmd.getName();
        this.deleted = false;

    }

    public void createRolePermission(List<UUID> permissionIds){
        if(permissionIds!=null &&!permissionIds.isEmpty()){
            permissionIds.stream()
                    .map(permissionId -> rolePermissions.add(new RolePermission(this.roleId,permissionId)));
        }
    }

    public void updateRolePermissions(List<UUID> newPermissionIds) {
        if (newPermissionIds != null && !newPermissionIds.isEmpty()) {
            // Extract the role IDs from the existing RolePermission list
            List<UUID> oldPermissionIds = this.rolePermissions.stream()
                    .map(RolePermission::getRoleId)
                    .toList();

            // Filter newRoleIds to exclude ones that are already in existingRoleIds
            List<RolePermission> newRolePermissions = newPermissionIds.stream()
                    .filter(permissionId -> !oldPermissionIds.contains(permissionId))
                    .map(permissionId -> new RolePermission(this.roleId,permissionId))
                    .toList();

            // Add the new RolePermissions to this user's roles
            this.rolePermissions.addAll(newRolePermissions);
        }
    }
}
