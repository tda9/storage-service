package org.example.daiam.infrastruture.persistence.domain_entity_mapper;

import org.example.daiam.domain.User;
import org.example.daiam.infrastruture.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserDomainAndEntityMapper extends DomainAndEntityMapper<User, UserEntity> {
    @Mapping(source = "root", target = "isRoot")//TODO: tai sao lal la root voi isRoot
    @Mapping(source = "verified", target = "isVerified")
    @Mapping(source = "lock", target = "isLock")
    @Mapping(target = "createdBy", source = "createdBy")
    User toDomain(UserEntity entity);
}
