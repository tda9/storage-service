package org.example.daiam.infrastruture.domainrepository;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.daiam.domain.Role;
import org.example.daiam.domain.RolePermission;
import org.example.daiam.infrastruture.persistence.domain_entity_mapper.RoleDomainAndEntityMapper;
import org.example.daiam.infrastruture.persistence.domain_entity_mapper.RolePermissionDomainAndEntityMapper;
import org.example.daiam.infrastruture.persistence.entity.RoleEntity;
import org.example.daiam.infrastruture.persistence.entity.RolePermissionEntity;
import org.example.daiam.infrastruture.persistence.repository.RoleEntityRepository;
import org.example.daiam.infrastruture.persistence.repository.RolePermissionEntityRepository;
import org.example.daiam.infrastruture.support.dto.RoleDto;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class RoleDomainRepositoryImpl {
    private final RoleEntityRepository roleEntityRepository;
    private final RolePermissionEntityRepository rolePermissionEntityRepository;
    private final RoleDomainAndEntityMapper roleDomainAndEntityMapper;
    private final RolePermissionDomainAndEntityMapper rolePermissionDomainAndEntityMapper;

    @Transactional
    public Role save(Role domain) {
        if (roleEntityRepository.existsById(domain.getRoleId())) {
            List<RolePermissionEntity> rolePermissionEntities = rolePermissionEntityRepository.findAllByRoleId(domain.getRoleId()).orElse(null);
            if(rolePermissionEntities != null)
            {
                rolePermissionEntities.forEach(rolePermissionEntity -> rolePermissionEntity.setDeleted(true));
                rolePermissionEntityRepository.saveAll(rolePermissionEntities);
            }
        }
        RoleEntity entity = roleDomainAndEntityMapper.toEntity(domain);
        roleEntityRepository.save(entity);
        if (!domain.getRolePermissions().isEmpty()) {
            List<RolePermissionEntity> rolePermissionEntities = domain.getRolePermissions().stream().map(rolePermissionDomainAndEntityMapper::toEntity).toList();
            rolePermissionEntityRepository.saveAll(rolePermissionEntities);
        }
        return domain;
    }
    public List<UUID> getRoleIdsByNames(Set<String> names) {
        if (names == null || names.isEmpty()) {
            return Collections.emptyList(); // Return an empty list instead of null
        }
        return names.stream()
                .map(name -> {
                    // Retrieve the RoleDto and handle the optional
                    RoleDto roleDto = roleEntityRepository.getRoleIdAndDeletedByName(name)
                            .orElseThrow(() -> new NotFoundException("Role: " + name + " not found"));
                    // Check if the role is marked as deleted
                    if (roleDto.deleted()) {
                        throw new NotFoundException("Role: " + name + " is deleted");
                    }
                    // Return the role ID
                    return roleDto.roleId();
                })
                .toList();
    }
    @Transactional
    public Role getById(UUID RoleId) {
        RoleEntity RoleEntity = roleEntityRepository.findById(RoleId).orElseThrow(() -> new NotFoundException("Role not found"));
        Role role = roleDomainAndEntityMapper.toDomain(RoleEntity);
        role.setRolePermissions(enrichRolePermissions(role));
        return role;
    }

    public List<RolePermission> enrichRolePermissions(Role domain) {
        Optional<List<RolePermissionEntity>> RoleRoleEntities = rolePermissionEntityRepository.findAllByRoleId(domain.getRoleId());
        return RoleRoleEntities.map(roleEntities -> roleEntities.stream().map(rolePermissionDomainAndEntityMapper::toDomain).toList()).orElse(null);
    }
}
