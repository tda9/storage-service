package org.example.daiam.application.request_command_mapper;



import org.example.daiam.application.dto.request.CreateUserRequest;
import org.example.daiam.application.dto.request.UpdateUserRequest;
import org.example.daiam.domain.command.CreateUserCommand;
import org.example.daiam.domain.command.UpdateUserCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserRequestAndCommandMapper {
    CreateUserCommand toCreateCommand(CreateUserRequest request);
    UpdateUserCommand toUpdateCommand(UpdateUserRequest request);
}
