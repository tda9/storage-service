package org.example.daiam.application.service;

import org.example.daiam.application.dto.request.CreateRoleRequest;
import org.example.daiam.application.dto.request.DeleteRoleRequest;
import org.example.daiam.application.dto.request.UpdateRoleRequest;
import org.example.daiam.domain.Role;

public interface RoleCommandService {
    Role create(CreateRoleRequest request);
    Role updateById(UpdateRoleRequest request,String id);
    boolean deleteById(DeleteRoleRequest request, String id);
}
