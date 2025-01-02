package org.example.daiam.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.example.daiam.application.dto.request.CreateRoleRequest;
import org.example.daiam.application.dto.request.DeleteRoleRequest;
import org.example.daiam.application.dto.request.UpdateRoleRequest;
import org.example.daiam.application.request_command_mapper.RoleRequestAndCommandMapper;
import org.example.daiam.application.service.others.CommonService;
import org.example.daiam.application.service.RoleCommandService;
import org.example.daiam.domain.Role;
import org.example.daiam.domain.command.CreateRoleCommand;
import org.example.daiam.domain.command.UpdateRoleCommand;
import org.example.daiam.domain.command_domain_mapper.RoleCommandAndDomainMapper;
import org.example.daiam.infrastruture.domainrepository.impl.PermissionDomainRepositoryImpl;
import org.example.daiam.infrastruture.domainrepository.impl.RoleDomainRepositoryImpl;
import org.example.daiam.infrastruture.persistence.repository.RolePermissionEntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleCommandServiceImpl implements RoleCommandService {
    private final RoleRequestAndCommandMapper roleRequestAndCommandMapper;
    private final RoleDomainRepositoryImpl roleDomainRepositoryImpl;
    private final PermissionDomainRepositoryImpl permissionDomainRepositoryImpl;
    private final CommonService commonService;

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
        UUID id = commonService.isValidUUID(roleId);
        Set<String> newPermissionNames = updateRequest.permissionsResourceName();
        UpdateRoleCommand command = roleRequestAndCommandMapper.toUpdateCommand(updateRequest);
        if (!CollectionUtils.isEmpty(newPermissionNames)) {
            command.setPermissionIds(permissionDomainRepositoryImpl.getPermissionIdsByNames(newPermissionNames));
        }
        Role domain = roleDomainRepositoryImpl.getById(id);
        domain.update(command);
        return roleDomainRepositoryImpl.save(domain);
    }

    @Override
    public Role deleteById(DeleteRoleRequest request, String id) {
        UUID roleId = commonService.isValidUUID(id);
        Role domain = roleDomainRepositoryImpl.getById(roleId);
        return roleDomainRepositoryImpl.save(domain);
    }
}
