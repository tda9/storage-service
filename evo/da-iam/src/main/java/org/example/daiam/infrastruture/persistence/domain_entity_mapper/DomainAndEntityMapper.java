package org.example.daiam.infrastruture.persistence.domain_entity_mapper;

import org.mapstruct.Mapper;


public interface DomainAndEntityMapper<D,E> {
    D toDomain(E entity);
    E toEntity(D domain);
}
