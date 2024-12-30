package org.example.daiam.application.service.impl;

import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.example.daiam.application.dto.request.CreateRoleRequest;
import org.example.daiam.application.dto.request.DeleteRoleRequest;
import org.example.daiam.application.dto.request.UpdateRoleRequest;
import org.example.daiam.application.request_command_mapper.RoleRequestAndCommandMapper;
import org.example.daiam.application.service.RoleCommandService;
import org.example.daiam.domain.Role;
import org.example.daiam.domain.command.CreateRoleCommand;
import org.example.daiam.domain.command.UpdateRoleCommand;
import org.example.daiam.domain.command_domain_mapper.RoleCommandAndDomainMapper;
import org.example.daiam.infrastruture.domainrepository.PermissionDomainRepositoryImpl;
import org.example.daiam.infrastruture.domainrepository.RoleDomainRepositoryImpl;
import org.example.daiam.infrastruture.persistence.repository.RolePermissionEntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleCommandServiceImpl implements RoleCommandService {
    private final RoleCommandAndDomainMapper roleCommandAndDomainMapper;
    private final RoleRequestAndCommandMapper roleRequestAndCommandMapper;
    private final RoleDomainRepositoryImpl roleDomainRepositoryImpl;
    private final PermissionDomainRepositoryImpl permissionDomainRepositoryImpl;
private final RolePermissionEntityRepository permissionEntityRepository;
    @Override
    public Role create(CreateRoleRequest createRequest) {
        //req to cmd
        CreateRoleCommand createRoleCommand = roleRequestAndCommandMapper.toCreateCommand(createRequest);
        List<UUID> cmdPermissionIds = permissionDomainRepositoryImpl.getPermissionIdsByNames(createRequest.permissionsResourceName());
        createRoleCommand.setPermissionsIds(cmdPermissionIds);
        //cmd to domain
        Role domain = new Role(createRoleCommand);
        return roleDomainRepositoryImpl.save(domain);
    }

    @Override
    @Transactional
    public Role updateById(UpdateRoleRequest updateRequest, String roleId) {
        //req to cmd
        UpdateRoleCommand updateRoleCommand = roleRequestAndCommandMapper.toUpdateCommand(updateRequest);
        List<UUID> cmdPermissionIds = permissionDomainRepositoryImpl.getPermissionIdsByNames(updateRequest.permissionsResourceName());
        updateRoleCommand.setPermissionIds(cmdPermissionIds);
        Role domain = roleDomainRepositoryImpl.getById(UUID.fromString(roleId));//TODO: check string RoleId before parse to UUID
        //cmd to domain
        roleCommandAndDomainMapper.toUpdateCommand(updateRoleCommand, domain);
        domain.updateRolePermissions(updateRoleCommand.getPermissionIds());
        return roleDomainRepositoryImpl.save(domain);
    }

    @Override
    public boolean deleteById(DeleteRoleRequest request, String id) {
        UUID roleId= isValidUUID(id);
        return roleDomainRepositoryImpl.deleteById(roleId);
    }
    private UUID isValidUUID(String uuid) {
        try {
            return UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid UUID");
        }
    }
}
