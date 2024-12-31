package org.example.daiam.application.service;


import org.example.daiam.application.dto.request.CreateUserRequest;
import org.example.daiam.application.dto.request.UpdateUserRequest;
import org.example.daiam.domain.User;


public interface UserCommandService {
    User update(UpdateUserRequest request, String userId);
    User create(CreateUserRequest request);
    User delete(String id);
}
