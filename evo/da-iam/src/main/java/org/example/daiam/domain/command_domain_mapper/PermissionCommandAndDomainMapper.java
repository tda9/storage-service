package org.example.daiam.domain.command_domain_mapper;

import org.example.daiam.domain.Permission;
import org.example.daiam.domain.command.UpdatePermissionCommand;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PermissionCommandAndDomainMapper {
    void toUpdateCommand(UpdatePermissionCommand cmd, @MappingTarget Permission permission);

}
