package org.example.daiam.infrastruture.persistence.repository;

import org.example.daiam.domain.User;
import org.example.daiam.infrastruture.persistence.entity.UserEntity;
import org.example.daiam.infrastruture.persistence.repository.custom.UserEntityRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, UUID>, UserEntityRepositoryCustom {
    boolean existsByEmail(String email);

    Optional<UserEntity> findByEmail(String email);
}
