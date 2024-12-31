package org.example.daiam.domain;

import lombok.*;
import org.example.daiam.audit.entity.AuditDomain;
import org.example.daiam.audit.entity.AuditEntity;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRole extends AuditDomain {
    private UUID id;
    private UUID userId;
    private UUID roleId;
    private Boolean deleted;

    public UserRole(UUID userId, UUID roleId){
        id = UUID.randomUUID();
        this.userId = userId;
        this.roleId = roleId;
        deleted = false;
    }
}
