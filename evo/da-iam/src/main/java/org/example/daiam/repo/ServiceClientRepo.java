package org.example.daiam.repo;

import org.example.daiam.dto.response.DefaultClientTokenResponse;
import org.example.daiam.entity.ServiceClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceClientRepo extends JpaRepository<ServiceClient,String> {
    @Query("SELECT sc FROM ServiceClient sc WHERE sc.clientId = :clientId AND sc.clientSecret = :clientSecret")
    Optional<ServiceClient> findByClientIdAndClientSecret(@Param("clientId") String clientId, @Param("clientSecret") String clientSecret);
}
