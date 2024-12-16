package org.example.model.dto.mapper;

import org.mapstruct.Mapper;

//@Mapper(componentModel = "spring")
public interface BaseMapper<E,R> {
    E toEntity(R request);
}
