package org.example.daiam.infrastruture.persistence.domain_entity_mapper;

import org.example.daiam.domain.RolePermission;
import org.example.daiam.infrastruture.persistence.entity.RolePermissionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RolePermissionDomainAndEntityMapper extends DomainAndEntityMapper<RolePermission, RolePermissionEntity>{
}
