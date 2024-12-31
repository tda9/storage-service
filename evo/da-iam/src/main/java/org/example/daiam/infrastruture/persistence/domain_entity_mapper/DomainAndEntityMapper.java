package org.example.daiam.infrastruture.persistence.domain_entity_mapper;

import org.example.daiam.audit.entity.AuditDomain;
import org.example.daiam.audit.entity.AuditEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


public interface DomainAndEntityMapper<D,E> {

    D toDomain(E entity);
    E toEntity(D domain);
}
