package org.example.daiam.service.impl;

import org.example.daiam.dto.request.CreateUserRequest;
import org.example.daiam.dto.request.UpdateUserRequest;
import org.example.daiam.dto.response.KeycloakTokenResponse;

import org.example.daiam.entity.User;
import org.example.daiam.exception.ErrorResponseException;
import org.example.daiam.repo.BlackListTokenRepo;
import org.example.daiam.repo.RoleRepo;
import org.example.daiam.repo.UserRepo;
import org.example.daiam.repo.UserRoleRepo;

import org.example.daiam.service.*;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
@Slf4j
@Service
public class KeycloakUserService extends BaseKeycloakService implements BaseUserService {
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserRoleRepo userRoleRepo;
    private final PasswordService passwordService;
    private final UserService userService;
    private final KeycloakAuthenticationService keycloakAuthenticationService;

    public KeycloakUserService(Keycloak keycloak,
                               UserRepo userRepo,
                               RoleRepo roleRepo,
                               PasswordEncoder passwordEncoder,
                               EmailService emailService,
                               UserRoleRepo userRoleRepo,
                               PasswordService passwordService,
                               JWTService jwtService,
                               BlackListTokenRepo blackListTokenRepo,
                               KeycloakAuthenticationService keycloakAuthenticationService,
                               UserService userService) {
        super(keycloak,userRepo, roleRepo,blackListTokenRepo,jwtService);
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.userRoleRepo = userRoleRepo;
        this.passwordService = passwordService;
        this.userService = userService;
        this.keycloakAuthenticationService = keycloakAuthenticationService;
    }

    @Override
    public User create(CreateUserRequest request) {
        try {
        checkEmailExisted(request.email());
        List<UUID> rolesId = getRoles(request.role());//check hop le cac role co trong db ko va tra ve list id cua cac role
        String generatedPassword = passwordService.generateToken();
        User newUser = User.builder()//khoi tao user,
                .dob(request.dob())
                .image(null)
                .phone(request.phone())
                .email(request.email())
                .username(request.username())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .password(passwordEncoder.encode(generatedPassword))
                .build();
            createKeycloakUser(request.email(), generatedPassword);
            User user = userRepo.save(newUser);//save user
            emailService.sendEmail(request.email(), "Your IAM Service Password", generatedPassword);//gui mat khau cho user
            rolesId.forEach(roleId -> userRoleRepo.saveUserRole(user.getUserId(), roleId));
            KeycloakTokenResponse tokenResponse = keycloakAuthenticationService.getKeycloakUserToken(user.getEmail(), generatedPassword);
            emailService.sendConfirmationRegistrationEmail(request.email(), tokenResponse.getAccessToken());

            return user;
        } catch (Exception e) {
            throw new ErrorResponseException("Create failed: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public User updateById(UpdateUserRequest request) {
        String oldEmail = userRepo.findById(UUID.fromString(request.userId()))
                .orElseThrow(()-> new IllegalArgumentException("User id not found"))
                .getEmail();
        updateKeycloakUser(request,oldEmail);
        return userService.updateById(request);
    }

}
