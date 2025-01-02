package org.example.daiam.application.service.impl;

import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.example.daiam.application.service.PermissionQueryService;
import org.example.daiam.domain.Permission;
import org.example.daiam.infrastruture.domainrepository.impl.PermissionDomainRepositoryImpl;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionQueryServiceImpl implements PermissionQueryService {
    private final PermissionDomainRepositoryImpl permissionDomainRepositoryImpl;
    @Override
    public Permission getById(String id) {
        UUID uuid = isValidUUID(id);
        return permissionDomainRepositoryImpl.getById(uuid);
    }

    private UUID isValidUUID(String uuid) {
        try {
            return UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid UUID");
        }
    }
}
