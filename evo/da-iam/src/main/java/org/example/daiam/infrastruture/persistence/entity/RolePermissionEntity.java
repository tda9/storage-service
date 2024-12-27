package org.example.daiam.infrastruture.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.daiam.infrastruture.support.Scope;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "role_permissions")
@Entity
public class RolePermissionEntity {
    @Id
    @Column(name = "id")
    private UUID id;
    @Column(name = "role_id")
    private UUID roleId;
    @Column(name = "permission_id")
    private UUID permissionId;
    @Column(name = "resource_code")
    private String resourceCode;
    @Column(name = "scope")
    @Enumerated(EnumType.STRING)
    private Scope scope;
    private boolean deleted;
}
