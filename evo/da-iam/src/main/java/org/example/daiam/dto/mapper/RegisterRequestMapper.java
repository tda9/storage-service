package org.example.daiam.dto.mapper;

import org.example.daiam.dto.request.RegisterRequest;
import org.example.daiam.entity.User;
import org.example.model.dto.mapper.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RegisterRequestMapper extends BaseMapper<User,RegisterRequest>{

}
