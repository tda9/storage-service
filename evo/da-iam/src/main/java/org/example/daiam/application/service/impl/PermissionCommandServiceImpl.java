package org.example.daiam.application.service.impl;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.example.daiam.application.dto.request.CreatePermissionRequest;
import org.example.daiam.application.dto.request.UpdatePermissionRequest;
import org.example.daiam.application.request_command_mapper.PermissionRequestAndCommandMapper;
import org.example.daiam.application.service.PermissionCommandService;
import org.example.daiam.domain.Permission;
import org.example.daiam.domain.command.CreatePermissionCommand;
import org.example.daiam.domain.command.UpdatePermissionCommand;
import org.example.daiam.infrastruture.domainrepository.impl.PermissionDomainRepositoryImpl;
import org.example.daiam.infrastruture.persistence.domain_entity_mapper.PermissionDomainAndEntityMapper;
import org.example.daiam.infrastruture.persistence.entity.PermissionEntity;
import org.example.daiam.infrastruture.persistence.repository.PermissionEntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionCommandServiceImpl implements PermissionCommandService {
    private final PermissionRequestAndCommandMapper permissionRequestAndCommandMapper;
    private final PermissionDomainRepositoryImpl permissionDomainRepositoryImpl;
    private final PermissionEntityRepository permissionEntityRepository;
    private final PermissionDomainAndEntityMapper permissionDomainAndEntityMapper;

    @Override
    public Permission create(CreatePermissionRequest createRequest) {
        //req to cmd
        isResourceNameValid(createRequest.resourceName(),null);
        CreatePermissionCommand createPermissionCommand = permissionRequestAndCommandMapper.toCreateCommand(createRequest);
        //cmd to domain
        Permission domain = new Permission(createPermissionCommand);
        return permissionDomainRepositoryImpl.save(domain);
    }

    @Override
    @Transactional
    public Permission updateById(String id, UpdatePermissionRequest request) {
        //req to cmd
        UUID permissionId = isValidUUID(id);
        isResourceNameValid(request.resourceName(),permissionId);
        UpdatePermissionCommand command = permissionRequestAndCommandMapper.toUpdateCommand(request);

        PermissionEntity entity = permissionEntityRepository.findById(permissionId)
                .orElseThrow(() -> new NotFoundException("Permission not found"));//TODO: check string PermissionId before parse to UUID
        Permission domain = permissionDomainAndEntityMapper.toDomain(entity);
        //cmd to domain
        domain.update(command);
        return permissionDomainRepositoryImpl.save(domain);
    }

    @Override
    public Permission deleteById(String id) {
        UUID permissionId = isValidUUID(id);
        Permission domain = permissionDomainRepositoryImpl.getById(permissionId);
        domain.delete();
        return permissionDomainRepositoryImpl.save(domain);
    }

    private UUID isValidUUID(String uuid) {
        try {
            return UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid UUID");
        }
    }

    private void isResourceNameValid(String resourceName, UUID id) {
        if (id != null) {
            if (permissionEntityRepository.existsByResourceNameAndPermissionIdNot(resourceName, id)) {
                throw new BadRequestException("Resource name existed");
            }
        }
        if (permissionEntityRepository.existsByResourceName(resourceName)) {
            throw new BadRequestException("Resource name existed");
        }
    }
}
