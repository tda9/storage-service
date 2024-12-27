package org.example.daiam.domain;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Table(name = "service_client")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Client {
    private UUID clientId;
    private String clientSecret;
    private String clientHost;
}
