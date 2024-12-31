package org.example.daiam.application.service;

import org.example.daiam.application.dto.request.CreatePermissionRequest;
import org.example.daiam.application.dto.request.UpdatePermissionRequest;
import org.example.daiam.domain.Permission;

public interface PermissionCommandService {
    Permission create(CreatePermissionRequest request);
    Permission updateById(String id, UpdatePermissionRequest request);
    Permission deleteById(String id);
}
