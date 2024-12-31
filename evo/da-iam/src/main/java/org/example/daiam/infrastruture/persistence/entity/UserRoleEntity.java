package org.example.daiam.infrastruture.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.daiam.audit.entity.AuditEntity;

import java.util.UUID;

@Getter
@Setter
@Table(name = "user_roles")
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserRoleEntity extends AuditEntity {

    @Id
    private UUID id;
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "role_id")
    private UUID roleId;
    private boolean deleted;

}
