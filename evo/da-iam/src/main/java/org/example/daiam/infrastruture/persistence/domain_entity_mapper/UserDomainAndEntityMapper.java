package org.example.daiam.infrastruture.persistence.domain_entity_mapper;
import org.example.daiam.domain.User;
import org.example.daiam.infrastruture.persistence.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDomainAndEntityMapper extends DomainAndEntityMapper<User, UserEntity> {

}
