//package org.example.daiam.service.impl;
//
//import jakarta.ws.rs.NotFoundException;
//import org.example.daiam.dto.mapper.UserRequestMapper;
//import org.example.daiam.dto.request.CreateUserRequest;
//import org.example.daiam.dto.request.UpdateUserRequest;
//
//import org.example.daiam.entity.User;
//import org.example.daiam.repo.BlackListTokenRepo;
//import org.example.daiam.repo.RoleRepo;
//import org.example.daiam.repo.UserRepo;
//import org.example.daiam.repo.UserRoleRepo;
//
//import org.example.daiam.service.*;
//import lombok.extern.slf4j.Slf4j;
//import org.example.web.support.RedisService;
//import org.keycloak.admin.client.Keycloak;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.*;
//
//@Slf4j
//@Service
//public class KeycloakUserService extends BaseKeycloakService implements BaseUserService {
//    private final PasswordEncoder passwordEncoder;
//    private final EmailService emailService;
//    private final UserRoleRepo userRoleRepo;
//    private final PasswordService passwordService;
//    private final UserService userService;
//    private final KeycloakAuthenticationService keycloakAuthenticationService;
//    private final UserRequestMapper userRequestMapper;
//
//    public KeycloakUserService(Keycloak keycloak,
//                               UserRepo userRepo,
//                               RoleRepo roleRepo,
//                               PasswordEncoder passwordEncoder,
//                               EmailService emailService,
//                               UserRoleRepo userRoleRepo,
//                               PasswordService passwordService,
//                               JWTService jwtService,
//                               KeycloakAuthenticationService keycloakAuthenticationService,
//                               UserService userService, UserRequestMapper userRequestMapper, RedisService redisService) {
//        super(keycloak, userRepo, roleRepo,jwtService,redisService);
//        this.passwordEncoder = passwordEncoder;
//        this.emailService = emailService;
//        this.userRoleRepo = userRoleRepo;
//        this.passwordService = passwordService;
//        this.userService = userService;
//        this.keycloakAuthenticationService = keycloakAuthenticationService;
//        this.userRequestMapper = userRequestMapper;
//    }
//
//    @Override
//    @Transactional
//    public User create(CreateUserRequest request) {
//            checkExistedEmail(request.email());
//            Set<String> requestRoles = request.roles();
//            List<UUID> rolesId = (requestRoles == null || requestRoles.isEmpty()) ? null : getRoles(requestRoles);
//            String generatedPassword = passwordService.generateToken();
//            User newUser = userRequestMapper.toEntity(request);
//            newUser.setPassword(passwordEncoder.encode(generatedPassword));
//            newUser.setVerified(true);
//            createKeycloakUser(request.email(), generatedPassword);
//            User user = userRepo.save(newUser);
//            if (rolesId != null) {
//                rolesId.forEach(roleId -> userRoleRepo.saveUserRole(user.getUserId(), roleId));
//            }
//            //emailService.sendEmail(request.email(), "Your IAM Service Password", generatedPassword);//gui mat khau cho user
//            return user;
//    }
//
//    @Override
//    @Transactional
//    public User updateById(UpdateUserRequest request,String userId) {
//        String oldEmail = userRepo.findById(UUID.fromString(userId))
//                .orElseThrow(() -> new NotFoundException("User id not found"))
//                .getEmail();
//        updateKeycloakUser(request, oldEmail);
//        return userService.updateById(request,userId);
//    }
//
//}
