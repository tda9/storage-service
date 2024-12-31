package org.example.daiam.infrastruture.persistence.domain_entity_mapper;

import org.example.daiam.domain.Permission;
import org.example.daiam.infrastruture.persistence.entity.PermissionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PermissionDomainAndEntityMapper extends DomainAndEntityMapper<Permission, PermissionEntity>{

    @Override
    @Mapping(target = "createdBy", source = "createdBy")
    Permission toDomain(PermissionEntity entity);
}
