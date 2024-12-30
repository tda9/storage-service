package org.example.daiam.service;

import org.example.daiam.domain.User;
import org.example.daiam.dto.request.CreateUserRequest;
import org.example.daiam.dto.request.UpdateUserRequest;



public interface BaseUserService {
    User create(CreateUserRequest request);

    User updateById(UpdateUserRequest request, String id);
}
