package org.example.daiam.infrastruture.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.example.daiam.audit.entity.AuditEntity;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "service_client")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientEntity extends AuditEntity {
    @Id
    @Column(name = "client_id")
    private UUID clientId;
    @Column(name = "client_secret")
    private String clientSecret;
    @Column(name = "client_host")
    private String clientHost;
}
