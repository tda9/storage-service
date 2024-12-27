package org.example.daiam.application.service.impl;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.daiam.application.dto.request.CreatePermissionRequest;
import org.example.daiam.application.dto.request.UpdatePermissionRequest;
import org.example.daiam.application.request_command_mapper.PermissionRequestAndCommandMapper;
import org.example.daiam.application.service.PermissionCommandService;
import org.example.daiam.domain.Permission;
import org.example.daiam.domain.command.CreatePermissionCommand;
import org.example.daiam.domain.command.UpdatePermissionCommand;
import org.example.daiam.domain.command_domain_mapper.PermissionCommandAndDomainMapper;
import org.example.daiam.infrastruture.domainrepository.PermissionDomainRepositoryImpl;
import org.example.daiam.infrastruture.persistence.domain_entity_mapper.PermissionDomainAndEntityMapper;
import org.example.daiam.infrastruture.persistence.entity.PermissionEntity;
import org.example.daiam.infrastruture.persistence.repository.PermissionEntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionCommandServiceImpl implements PermissionCommandService {
    private final PermissionCommandAndDomainMapper permissionCommandAndDomainMapper;
    private final PermissionRequestAndCommandMapper permissionRequestAndCommandMapper;
    private final PermissionDomainRepositoryImpl permissionDomainRepositoryImpl;
    private final PermissionEntityRepository permissionEntityRepository;
    private final PermissionDomainAndEntityMapper permissionDomainAndEntityMapper;


    public Permission create(CreatePermissionRequest createRequest) {
        //req to cmd
        CreatePermissionCommand createPermissionCommand = permissionRequestAndCommandMapper.toCreateCommand(createRequest);
        //cmd to domain
        Permission domain = new Permission(createPermissionCommand);
        return permissionDomainRepositoryImpl.save(domain);
    }

    @Transactional
    public Permission updateById(UpdatePermissionRequest updateRequest, String PermissionId) {
        //req to cmd
        UpdatePermissionCommand updatePermissionCommand = permissionRequestAndCommandMapper.toUpdateCommand(updateRequest);

        PermissionEntity entity = permissionEntityRepository.findById(UUID.fromString(PermissionId)).orElseThrow(()-> new NotFoundException("Permission not found"));//TODO: check string PermissionId before parse to UUID
        Permission domain = permissionDomainAndEntityMapper.toDomain(entity);
        //cmd to domain
        permissionCommandAndDomainMapper.toUpdateCommand(updatePermissionCommand, domain);
        return permissionDomainRepositoryImpl.save(domain);
    }
}
