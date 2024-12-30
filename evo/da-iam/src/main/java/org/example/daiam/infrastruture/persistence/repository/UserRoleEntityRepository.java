package org.example.daiam.infrastruture.persistence.repository;

import org.example.daiam.infrastruture.persistence.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRoleEntityRepository extends JpaRepository<UserRoleEntity, Integer> {
    Optional<List<UserRoleEntity>> findByUserIdAndDeletedFalse(UUID userId);

    @Modifying
    @Query("UPDATE UserRoleEntity u SET u.deleted = true WHERE u.userId = :userId AND u.roleId = :roleId")
    int deleteByUserIdAndRoleId(UUID userId, UUID roleId);
}
