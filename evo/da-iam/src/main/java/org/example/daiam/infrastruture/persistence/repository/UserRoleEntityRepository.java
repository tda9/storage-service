package org.example.daiam.infrastruture.persistence.repository;

import org.example.daiam.infrastruture.persistence.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRoleEntityRepository extends JpaRepository<UserRoleEntity, Integer> {
    Optional<List<UserRoleEntity>> findByUserId(UUID userId);
}
