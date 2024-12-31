package org.example.daiam.domain;

import lombok.*;
import org.example.daiam.audit.entity.AuditDomain;
import org.example.daiam.audit.entity.AuditEntity;
import org.example.daiam.infrastruture.support.Scope;


import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RolePermission extends AuditDomain {
    private UUID id;
    private UUID roleId;
    private UUID permissionId;
    private String resourceCode;
    private Scope scope;
    private boolean deleted;

    public RolePermission(UUID roleId,UUID permissionId){
        this.roleId = roleId;
        this.permissionId = permissionId;
    }
}
