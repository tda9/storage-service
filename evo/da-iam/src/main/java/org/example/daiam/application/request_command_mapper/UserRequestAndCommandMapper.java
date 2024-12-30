package org.example.daiam.application.request_command_mapper;



import org.example.daiam.application.dto.request.CreateUserRequest;
import org.example.daiam.application.dto.request.RegisterRequest;
import org.example.daiam.application.dto.request.UpdateUserRequest;
import org.example.daiam.domain.command.CreateUserCommand;
import org.example.daiam.domain.command.UpdateUserCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserRequestAndCommandMapper {
    CreateUserCommand toCommand(CreateUserRequest request);
    CreateUserCommand toCommand(RegisterRequest request);
    UpdateUserCommand toCommand(UpdateUserRequest request);
}
