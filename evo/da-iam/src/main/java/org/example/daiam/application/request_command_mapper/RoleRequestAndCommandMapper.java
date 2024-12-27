package org.example.daiam.application.request_command_mapper;


import org.example.daiam.application.dto.request.CreateRoleRequest;
import org.example.daiam.application.dto.request.UpdateRoleRequest;
import org.example.daiam.domain.command.CreateRoleCommand;
import org.example.daiam.domain.command.UpdateRoleCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleRequestAndCommandMapper {
    CreateRoleCommand toCreateCommand(CreateRoleRequest request);
    UpdateRoleCommand toUpdateCommand(UpdateRoleRequest request);
}