package org.example.daiam.application.dto.mapper;

import org.example.daiam.application.dto.response.UserDto;
import org.example.daiam.domain.User;
import org.example.daiam.infrastruture.persistence.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {
    UserDto toDto(UserEntity entity);
    UserDto toDto(User domain);
}
