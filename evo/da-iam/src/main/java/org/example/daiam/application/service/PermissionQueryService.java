package org.example.daiam.application.service;

import org.example.daiam.domain.Permission;

public interface PermissionQueryService {
    Permission getById(String id);
}
