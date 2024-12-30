package org.example.daiam.infrastruture.persistence.repository;

import org.example.daiam.infrastruture.persistence.entity.RolePermissionEntity;
import org.example.daiam.infrastruture.support.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface RolePermissionEntityRepository extends JpaRepository<RolePermissionEntity,UUID> {

    Optional<List<RolePermissionEntity>> findAllByRoleIdIn(Set<UUID> roleIds);
    Optional<List<RolePermissionEntity>> findAllByRoleId(UUID roleIds);
    @Modifying
    @Query("UPDATE RolePermissionEntity rp set rp.deleted = true where rp.permissionId = :permissionId and rp.roleId = :roleId")
    int deleteByRoleIdAndPermissionId(UUID roleId, UUID permissionId);
}
