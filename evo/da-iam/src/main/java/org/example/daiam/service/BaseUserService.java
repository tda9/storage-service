package org.example.daiam.service;

import org.example.daiam.dto.request.CreateUserRequest;
import org.example.daiam.dto.request.UpdateUserRequest;
import org.example.daiam.entity.User;


public interface BaseUserService {
    User create(CreateUserRequest request);

    User updateById(UpdateUserRequest request, String id);
}
