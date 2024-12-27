package org.example.daiam.domain.command_domain_mapper;

import org.example.daiam.domain.Role;
import org.example.daiam.domain.command.UpdateRoleCommand;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")

public interface RoleCommandAndDomainMapper {
    void toUpdateCommand(UpdateRoleCommand cmd, @MappingTarget Role role);
}
