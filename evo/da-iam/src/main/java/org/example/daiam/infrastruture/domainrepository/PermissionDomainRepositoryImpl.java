package org.example.daiam.infrastruture.domainrepository;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.daiam.domain.Permission;
import org.example.daiam.infrastruture.persistence.domain_entity_mapper.PermissionDomainAndEntityMapper;
import org.example.daiam.infrastruture.persistence.entity.PermissionEntity;
import org.example.daiam.infrastruture.persistence.repository.PermissionEntityRepository;
import org.example.daiam.infrastruture.support.dto.PermissionDto;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
@Repository
@RequiredArgsConstructor
public class PermissionDomainRepositoryImpl {
    private final PermissionEntityRepository permissionEntityRepository;
    private final PermissionDomainAndEntityMapper permissionDomainAndEntityMapper;

    @Transactional
    public Permission save(Permission domain) {
        PermissionEntity permissionEntity = permissionDomainAndEntityMapper.toEntity(domain);
        permissionEntityRepository.save(permissionEntity);
        return domain;
    }

    public List<UUID> getPermissionIdsByNames(Set<String> names) {
        if (names == null || names.isEmpty()) {
            return Collections.emptyList(); // Return an empty list instead of null
        }
        return names.stream()
                .map(name -> {
                    // Retrieve the RoleDto and handle the optional
                    PermissionDto permissionDto = permissionEntityRepository.findPermissionIdAndDeletedByResourceName(name)
                            .orElseThrow(() -> new NotFoundException("Permission: " + name + " not found"));
                    // Check if the role is marked as deleted
                    if (permissionDto.deleted()) {
                        throw new NotFoundException("Permission: " + name + " is deleted");
                    }
                    // Return the role ID
                    return permissionDto.permissionId();
                })
                .toList();
    }
}
