package org.example.daiam.infrastruture.domainrepository.impl;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.daiam.domain.Role;
import org.example.daiam.domain.RolePermission;
import org.example.daiam.infrastruture.domainrepository.DomainRepository;
import org.example.daiam.infrastruture.persistence.domain_entity_mapper.RoleDomainAndEntityMapper;
import org.example.daiam.infrastruture.persistence.domain_entity_mapper.RolePermissionDomainAndEntityMapper;
import org.example.daiam.infrastruture.persistence.entity.RoleEntity;
import org.example.daiam.infrastruture.persistence.entity.RolePermissionEntity;
import org.example.daiam.infrastruture.persistence.repository.RoleEntityRepository;
import org.example.daiam.infrastruture.persistence.repository.RolePermissionEntityRepository;
import org.example.daiam.infrastruture.support.dto.RoleDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RoleDomainRepositoryImpl implements DomainRepository<Role> {
    private final RoleEntityRepository roleEntityRepository;
    private final RolePermissionEntityRepository rolePermissionEntityRepository;
    private final RoleDomainAndEntityMapper roleDomainAndEntityMapper;
    private final RolePermissionDomainAndEntityMapper rolePermissionDomainAndEntityMapper;

    @Override
    @Transactional
    public Role save(Role domain) {
        RoleEntity entity = roleDomainAndEntityMapper.toEntity(domain);
        List<RolePermission> rolePermissions = domain.getRolePermissions();

        if (!CollectionUtils.isEmpty(rolePermissions)) {
            List<RolePermissionEntity> rolePermissionEntities = rolePermissions.stream()
                    .map(rolePermissionDomainAndEntityMapper::toEntity)
                    .toList();
            rolePermissionEntityRepository.saveAll(rolePermissionEntities);
        }
        roleEntityRepository.save(entity);
        return domain;
    }

    public List<UUID> getRoleIdsByNames(Set<String> names) {
        if(CollectionUtils.isEmpty(names)) return null;
        return names.stream()
                .map(name -> {
                    RoleDto roleDto = roleEntityRepository.getRoleIdAndDeletedByName(name)
                            .orElseThrow(() -> new NotFoundException("Role: " + name + " not found"));
                    if (roleDto.deleted()) {
                        throw new BadRequestException("Role: " + name + " is deleted");
                    }
                    return roleDto.roleId();
                }).toList();
    }

    @Override
    @Transactional
    public Role getById(UUID RoleId) {
        RoleEntity RoleEntity = roleEntityRepository.findById(RoleId).orElseThrow(() -> new NotFoundException("Role not found"));
        Role role = roleDomainAndEntityMapper.toDomain(RoleEntity);
        enrichRolePermissions(role);
        return role;
    }

    public void enrichRolePermissions(Role domain) {
        Optional<List<RolePermissionEntity>> roleRoleEntities = rolePermissionEntityRepository.findAllByRoleId(domain.getRoleId());
        if (roleRoleEntities.isPresent()) {
            List<RolePermission> rolePermissions = roleRoleEntities.get().stream()
                    .map(rolePermissionDomainAndEntityMapper::toDomain)
                    .toList();
            domain.setRolePermissions(rolePermissions);
        }
    }

    public boolean deleteAndCheck(UUID roleId, UUID permissionId) {
        return rolePermissionEntityRepository.deleteByRoleIdAndPermissionId(roleId, permissionId) > 0;
    }

    public boolean deleteById(UUID id) {
        return roleEntityRepository.deleteByRoleId(id) > 0;
    }
}
