package org.example.daiam.repo;

import org.example.daiam.entity.ServiceClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceClientRepo extends JpaRepository<ServiceClient, UUID> {
    @Query("SELECT sc FROM ServiceClient sc WHERE sc.clientId = :clientId AND sc.clientSecret = :clientSecret")
    Optional<ServiceClient> findByClientIdAndClientSecret(@Param("clientId") UUID clientId, @Param("clientSecret") String clientSecret);
}
