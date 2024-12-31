package org.example.daiam.infrastruture.persistence.repository;

import jakarta.transaction.Transactional;
import org.example.daiam.infrastruture.persistence.entity.PermissionEntity;
import org.example.daiam.infrastruture.persistence.repository.custom.PermissionEntityRepositoryCustom;
import org.example.daiam.infrastruture.support.Scope;
import org.example.daiam.infrastruture.support.dto.PermissionDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionEntityRepository extends JpaRepository<PermissionEntity, UUID>, PermissionEntityRepositoryCustom {

    @Query("SELECT new org.example.daiam.infrastruture.support.dto.PermissionDto(r.permissionId, r.deleted)" +
            " FROM PermissionEntity r WHERE r.resourceName =  :name")
    Optional<PermissionDto> findPermissionIdAndDeletedByResourceName(String name);

    boolean existsByResourceName(String resourceName);
    boolean existsByResourceNameAndPermissionIdNot(String resourceName,UUID permissionId);

    @Modifying
    @Query("UPDATE PermissionEntity r set r.deleted = true where r.permissionId = :permissionId")
    int deleteByPermissionId(UUID permissionId);
}
