package org.example.daiam.application.request_command_mapper;


import org.example.daiam.application.dto.request.CreatePermissionRequest;
import org.example.daiam.application.dto.request.UpdatePermissionRequest;
import org.example.daiam.domain.command.CreatePermissionCommand;
import org.example.daiam.domain.command.UpdatePermissionCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionRequestAndCommandMapper {
    CreatePermissionCommand toCreateCommand(CreatePermissionRequest request);
    UpdatePermissionCommand toUpdateCommand(UpdatePermissionRequest request);
}
