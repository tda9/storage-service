package org.example.daiam.service.impl;

import org.example.daiam.dto.mapper.UserRequestMapper;
import org.example.daiam.dto.request.CreateUserRequest;
import org.example.daiam.dto.request.FilterUsersRequest;
import org.example.daiam.dto.request.UpdateUserRequest;
import org.example.daiam.dto.response.DefaultAccessTokenResponse;

import org.example.daiam.entity.User;
import org.example.daiam.exception.ErrorResponseException;
import org.example.daiam.exception.UserNotFoundException;
import org.example.daiam.repo.BlackListTokenRepo;
import org.example.daiam.repo.RoleRepo;
import org.example.daiam.repo.UserRepo;
import org.example.daiam.repo.UserRoleRepo;
import org.example.daiam.repo.impl.UserRepoImpl;

import lombok.extern.slf4j.Slf4j;
import org.example.daiam.service.*;
import org.example.web.support.RedisService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class UserService extends BaseService implements BaseUserService {
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserRoleRepo userRoleRepo;
    private final PasswordService passwordService;
    private final UserRepoImpl userRepoImpl;
    private final UserRequestMapper userRequestMapper;


    public UserService(UserRepo userRepo,
                       RoleRepo roleRepo,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService,
                       UserRoleRepo userRoleRepo,
                       PasswordService passwordService,
                       UserRepoImpl userRepoImpl,
                       JWTService jwtService,
                       UserRequestMapper userRequestMapper,
                       RedisService  redisService) {
        super(userRepo, roleRepo,jwtService,redisService);
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.userRoleRepo = userRoleRepo;
        this.passwordService = passwordService;
        this.userRepoImpl = userRepoImpl;
        this.userRequestMapper = userRequestMapper;
    }

    @Override
    public User create(CreateUserRequest request) {
        try {
            checkExistedEmail(request.email());
            Set<String> requestRoles = request.roles();
            List<UUID> rolesId = (requestRoles == null || requestRoles.isEmpty()) ? null : getRoles(requestRoles);
            String generatedPassword = passwordService.generateToken();
            User newUser = userRequestMapper.toEntity(request);
            newUser.setPassword(passwordEncoder.encode(generatedPassword));
            User user = userRepo.save(newUser);
            if (rolesId != null) {
                rolesId.forEach(roleId -> userRoleRepo.saveUserRole(user.getUserId(), roleId));
            }
            DefaultAccessTokenResponse tokenResponse = generateDefaultToken(request.email(), user.getUserId());
            //emailService.sendEmail(request.email(), "Your IAM Service Password", generatedPassword);//gui mat khau cho user
            return user;
        } catch (Exception e) {
            throw new ErrorResponseException("Create failed: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public User updateById(UpdateUserRequest request,String userId) {
        UUID id = UUID.fromString(userId);
        Set<String> requestRoles = request.roles();
        List<UUID> rolesId = (requestRoles == null || requestRoles.isEmpty()) ? null : getRoles(requestRoles);
        User user = userRepo.findById(id).orElseThrow(() -> new UserNotFoundException("User not found during update"));
        if (userRepo.existsByEmailAndUserIdNot(request.email(), id)) {//kiem tra co trung permission khac ko
            throw new IllegalArgumentException("Email existed");
        }
        try {
            userRequestMapper.updateUserFromRequest(request,user);
            userRepo.save(user);
            if (rolesId != null) {
                rolesId.forEach(roleId -> userRoleRepo.saveUserRole(user.getUserId(), roleId));
            }
            return user;
        } catch (Exception ex) {
            log.error("Error at class UserService, function updateById: {}", ex.getMessage());
            throw new ErrorResponseException("Update user failed");
        }
    }

    public void save(User user) {
        userRepo.save(user);
    }

    private void isOperationSuccess(int isSuccess, String message) {
        if (isSuccess == 0) {
            log.error("Error at isOperationSuccess");
            throw new IllegalArgumentException(message);
        }
    }

    @Transactional
    public void addRolesToUser(UUID userId, List<UUID> roleIds) {
        userRoleRepo.deleteByUserId(userId);
        for (UUID roleId : roleIds) {
            userRoleRepo.insertUserRoles(userId, roleId);
        }
    }

    public List<User> searchByKeyword(String keyword, String sortBy, String sort, int currentSize, int currentPage) {
        return userRepoImpl.searchByKeyword(keyword, sortBy, sort, currentSize, currentPage);
    }

    public List<User> filter(FilterUsersRequest request, String sortBy, String sort, int currentSize, int currentPage) {
        return userRepoImpl.filterByField(request, sortBy, sort, currentSize, currentPage);
    }

    public User findById(String id) {
        return userRepo.findById(UUID.fromString(id)).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public User findByEmail(String email) {
        return userRepo.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public Long getTotalSize(String keyword) {
        return userRepoImpl.getTotalSize(keyword);
    }
    public Long getTotalFilterSize(FilterUsersRequest request) {
        return userRepoImpl.getTotalFilterSize(request);
    }

}
