package org.example.daiam.infrastruture.persistence.domain_entity_mapper;

import org.example.daiam.domain.Permission;
import org.example.daiam.infrastruture.persistence.entity.PermissionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionDomainAndEntityMapper extends DomainAndEntityMapper<Permission, PermissionEntity>{
}
