package org.example.daiam.domain.command_domain_mapper;

import org.example.daiam.domain.User;
import org.example.daiam.domain.command.UpdateUserCommand;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserCommandAndDomainMapper {
    void toUpdateCommand(UpdateUserCommand cmd, @MappingTarget User user);
}
