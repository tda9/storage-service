package org.example.daiam.infrastruture.persistence.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.example.daiam.infrastruture.persistence.repository.custom.RoleEntityRepositoryCustom;
import org.example.daiam.infrastruture.support.dto.RoleDto;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RoleEntityRepositoryImpl implements RoleEntityRepositoryCustom {
    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public Optional<RoleDto> getRoleIdAndDeletedByName(String name) {
        return Optional.ofNullable(entityManager.createQuery("SELECT new org.example.demo.infrastruture.support.dto.RoleDto(r.roleId, r.deleted) FROM RoleEntity r WHERE r.name = :name", RoleDto.class)
                .setParameter("name", name)
                .getSingleResult());
    }
}
