package org.example.daiam.infrastruture.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.daiam.infrastruture.support.Scope;

import java.util.UUID;


@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "permissions")
@Entity
public class PermissionEntity {
    @Id
    @Column(name = "permission_id")
    private UUID permissionId;
    @Column(name = "resource_name", nullable = false, unique = true)
    private String resourceName;
    @Column(name = "scope", nullable = false)
    @Enumerated(EnumType.STRING)
    private Scope scope;
    @Column(name = "resource_code", nullable = false)
    private String resourceCode;
    @Builder.Default
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;
}
