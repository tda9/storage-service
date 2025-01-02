package org.example.daiam.application.service.impl;

import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.example.daiam.application.service.RoleQueryService;
import org.example.daiam.domain.Role;
import org.example.daiam.infrastruture.domainrepository.impl.RoleDomainRepositoryImpl;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleQueryServiceImpl implements RoleQueryService {
    private final RoleDomainRepositoryImpl roleDomainRepositoryImpl;
    @Override
    public Role getById(String id) {
        UUID uuid = isValidUUID(id);
        return roleDomainRepositoryImpl.getById(uuid);
    }

    private UUID isValidUUID(String uuid) {
        try {
            return UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid UUID");
        }
    }
}
