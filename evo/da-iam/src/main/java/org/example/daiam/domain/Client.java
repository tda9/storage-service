package org.example.daiam.domain;

import jakarta.persistence.Table;
import lombok.*;
import org.example.daiam.audit.entity.AuditDomain;
import org.example.daiam.audit.entity.AuditEntity;

import java.util.UUID;


@EqualsAndHashCode(callSuper = true)
@Table(name = "service_client")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Client extends AuditDomain {
    private UUID clientId;
    private String clientSecret;
    private String clientHost;
}
