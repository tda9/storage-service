package org.example.daiam.infrastruture.persistence.domain_entity_mapper;

import org.example.daiam.domain.UserRole;
import org.example.daiam.infrastruture.persistence.entity.UserRoleEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserRoleDomainAndEntityMapper extends DomainAndEntityMapper<UserRole, UserRoleEntity>{

}
