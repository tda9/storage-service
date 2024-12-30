package org.example.daiam.infrastruture.persistence.repository;

import org.example.daiam.infrastruture.persistence.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ClientEntityRepository extends JpaRepository<ClientEntity, UUID> {

}
