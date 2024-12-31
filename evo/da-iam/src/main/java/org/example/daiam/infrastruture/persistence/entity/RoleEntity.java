package org.example.daiam.infrastruture.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.example.daiam.audit.entity.AuditEntity;

import java.util.UUID;


@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "roles")
@Builder
@AllArgsConstructor
public class RoleEntity extends AuditEntity {
    @Id
    @Column(name = "role_id")
    private UUID roleId;
    @Column(name = "name", nullable = false)
    private String name;
    @Builder.Default
    @Column(name = "deleted" ,nullable = false)
    private boolean deleted = false;
}
