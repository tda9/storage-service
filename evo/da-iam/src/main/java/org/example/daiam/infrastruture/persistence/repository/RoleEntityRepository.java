package org.example.daiam.infrastruture.persistence.repository;

import jakarta.transaction.Transactional;
import org.example.daiam.domain.Role;
import org.example.daiam.infrastruture.persistence.entity.RoleEntity;
import org.example.daiam.infrastruture.persistence.repository.custom.RoleEntityRepositoryCustom;
import org.example.daiam.infrastruture.support.dto.RoleDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface RoleEntityRepository extends JpaRepository<RoleEntity, UUID>, RoleEntityRepositoryCustom {

    @Override
    @Query("SELECT new org.example.daiam.infrastruture.support.dto.RoleDto(r.roleId, r.deleted)" +
            " FROM RoleEntity r WHERE LOWER(r.name) = LOWER(:name)")
    Optional<RoleDto> getRoleIdAndDeletedByName(String name);

    @Modifying
    @Query("UPDATE RoleEntity r set r.deleted = true where r.roleId = :roleId")
    int deleteByRoleId(UUID roleId);

        @Query("SELECT r FROM RoleEntity r " +
            "JOIN UserRoleEntity ur ON r.roleId = ur.roleId " +
            "WHERE ur.userId = :userId AND r.deleted = false")
    Optional<Set<RoleEntity>> findRolesByUserId(@Param("userId") UUID userId);
}
