package org.example.daiam.infrastruture.persistence.domain_entity_mapper;


import org.example.daiam.domain.Role;
import org.example.daiam.infrastruture.persistence.entity.RoleEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleDomainAndEntityMapper extends DomainAndEntityMapper<Role, RoleEntity>{

}
