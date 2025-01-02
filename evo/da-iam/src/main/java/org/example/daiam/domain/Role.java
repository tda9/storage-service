package org.example.daiam.domain;

import lombok.*;
import org.apache.commons.lang.StringUtils;
import org.example.daiam.audit.entity.AuditDomain;
import org.example.daiam.audit.entity.AuditEntity;
import org.example.daiam.domain.command.CreateRoleCommand;
import org.example.daiam.domain.command.UpdateRoleCommand;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Stream;


@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Role extends AuditDomain {
    private UUID roleId;
    private String name;
    private boolean deleted;
    List<RolePermission> rolePermissions = new ArrayList<>();

    public Role(CreateRoleCommand cmd) {
        this.roleId = UUID.randomUUID();
        this.name = cmd.getName();
        this.deleted = false;
        this.createRolePermission(cmd.getPermissionsIds());
    }

    public void update(UpdateRoleCommand cmd) {
        if (StringUtils.isNotBlank(cmd.getName())) {
            this.name = cmd.getName();
        }
        if (cmd.getDeleted() != null) {
            this.deleted = cmd.getDeleted();
        }
        if (!CollectionUtils.isEmpty(cmd.getPermissionIds())) {
            this.updateRolePermissions(cmd.getPermissionIds());
        }
    }

    public void delete() {
        this.setDeleted(true);
        this.deleteRolePermission();
    }

    public void createRolePermission(List<UUID> permissionIds) {
        if (CollectionUtils.isEmpty(permissionIds)) return;
        permissionIds.forEach(
                permissionId -> rolePermissions.add(new RolePermission(this.roleId, permissionId)));

    }

    public void updateRolePermissions(List<UUID> newPermissionIds) {
        if (CollectionUtils.isEmpty(newPermissionIds)) return;

        if (!CollectionUtils.isEmpty(this.rolePermissions)) {
            // Mark existing permissions as deleted if not in newPermissionIds
            this.rolePermissions = this.rolePermissions.stream()
                    .peek(rolePermission -> rolePermission.setDeleted(!newPermissionIds.contains(rolePermission.getPermissionId())))
                    .toList();

            // Filter newPermissionIds to include only those not already in rolePermissions
            List<UUID> toAdd = newPermissionIds.stream()
                    .filter(newPermissionId -> this.rolePermissions.stream()
                            .noneMatch(rolePermission -> rolePermission.getPermissionId().equals(newPermissionId)))
                    .toList();

            // Add new permissions to rolePermissions
            this.rolePermissions = Stream.concat(
                            this.rolePermissions.stream(),
                            toAdd.stream().map(permissionId -> new RolePermission(this.roleId, permissionId)))
                    .toList();
        } else {
            // Add all newPermissionIds if rolePermissions is empty
            this.rolePermissions = newPermissionIds.stream()
                    .map(permissionId -> new RolePermission(this.roleId, permissionId))
                    .toList();
        }
    }

    public void deleteRolePermission() {
        if (!CollectionUtils.isEmpty(this.rolePermissions)) {
            this.rolePermissions.forEach(rolePermission -> rolePermission.setDeleted(true));
        }
    }
}
