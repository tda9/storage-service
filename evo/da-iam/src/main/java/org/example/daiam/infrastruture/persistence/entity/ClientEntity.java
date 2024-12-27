package org.example.daiam.infrastruture.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "service_client")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientEntity {
    @Id
    @Column(name = "client_id")
    private UUID clientId;
    @Column(name = "client_secret")
    private String clientSecret;
    @Column(name = "client_host")
    private String clientHost;
}
