package org.example.daiam.infrastruture.persistence.repository;

import org.example.daiam.entity.ServiceClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientEntityRepository extends JpaRepository<ServiceClient, UUID> {

}
