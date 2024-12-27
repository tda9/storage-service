package org.example.daiam.infrastruture.persistence.repository.custom;

import org.example.daiam.infrastruture.support.dto.RoleDto;

import java.util.Optional;

public interface RoleEntityRepositoryCustom {
    Optional<RoleDto> getRoleIdAndDeletedByName(String name);
}
