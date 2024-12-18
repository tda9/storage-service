package org.example.daiam.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.example.daiam.dto.mapper.RegisterRequestMapper;
import org.example.daiam.dto.request.ChangePasswordRequest;
import org.example.daiam.dto.request.LoginRequest;
import org.example.daiam.dto.request.LogoutRequest;
import org.example.daiam.dto.request.RegisterRequest;
import org.example.daiam.entity.BlackListToken;
import org.example.model.dto.response.BaseTokenResponse;
import org.example.daiam.dto.response.DefaultClientTokenResponse;
import org.example.daiam.dto.response.DefaultAccessTokenResponse;
import org.example.daiam.entity.ServiceClient;
import org.example.daiam.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.example.daiam.repo.*;
import org.example.daiam.service.*;
import org.example.model.dto.response.BasedResponse;
import org.example.web.support.RedisService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class AuthenticationService extends BaseService implements BaseAuthenticationService {
    private final RegisterRequestMapper registerRequestMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserRoleRepo userRoleRepo;
    private final PasswordService passwordService;
    private final ServiceClientRepo serviceClientRepo;
    public AuthenticationService(UserRepo userRepo,
                                 RoleRepo roleRepo, RegisterRequestMapper registerRequestMapper,
                                 PasswordEncoder passwordEncoder,
                                 UserService userService,
                                 EmailService emailService,
                                 UserRoleRepo userRoleRepo,
                                 JWTService jwtService,
                                 PasswordService passwordService,
                                 ServiceClientRepo serviceClientRepo,
                                 RedisService redisService
    ) {
        super(userRepo, roleRepo,jwtService, redisService);
        this.registerRequestMapper = registerRequestMapper;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.userRoleRepo = userRoleRepo;
        this.passwordService = passwordService;
        this.serviceClientRepo = serviceClientRepo;
    }

    @Override
    @Transactional
    public User register(RegisterRequest request) {
        checkExistedEmail(request.email());
        Set<String> requestRoles = request.roles();
        List<UUID> rolesId = (requestRoles == null || requestRoles.isEmpty()) ? null : getRoles(requestRoles);
        User newUser = registerRequestMapper.toEntity(request);
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setVerified(true);
        newUser.setRoot(true);
        User user = userRepo.save(newUser);
        if (rolesId != null) {
            rolesId.forEach(roleId -> userRoleRepo.saveUserRole(user.getUserId(), roleId));
        }
        DefaultAccessTokenResponse tokenResponse = generateDefaultToken(request.email(), user.getUserId());
        //emailService.verifyEmail(request.email(), tokenResponse.getAccessToken());
        return user;
    }

    @Override
    public BaseTokenResponse login(LoginRequest request, HttpServletRequest servletRequest) {
        User userEntity = getUser(request.email(),"Login email not found");
        checkValidUser(userEntity);
        saveBlackAccessToken(servletRequest);//TODO: save refresh token to redis after the first time
        return generateDefaultToken(userEntity.getEmail(), userEntity.getUserId());
    }

    private void checkValidUser(User user) {
        if (!user.isVerified()) {
            //passwordService.emailSpamHandler(user);
            throw new BadRequestException("Email not verified");
        } else if (user.isLock()) {
            throw new BadRequestException("User is locked");
        } else if (user.isDeleted()) {
            throw new BadRequestException("User was deleted");
        } else if (passwordEncoder.matches(user.getPassword(), user.getPassword())) {
            throw new BadRequestException("Incorrect password");
        }
    }

    @Override
    public BaseTokenResponse refreshToken(String refreshToken, HttpServletRequest servletRequest) {
        if (jwtService.isRefreshTokenValid(refreshToken)) {
            String email = jwtService.extractEmail(refreshToken);
            User user = getUser(email,"User not found in refresh token");
            checkExistedEmail(user.getEmail());
            saveBlackAccessToken(servletRequest);
            return generateDefaultToken(user.getEmail(), user.getUserId());
        }
        throw new BadRequestException("Invalid refresh token");
    }

    public ResponseEntity<?> logout(LogoutRequest request, HttpServletRequest servletRequest) {
        saveBlackRefreshToken(servletRequest,request.refreshToken());
        return ResponseEntity.ok(BasedResponse.success("Logout successful",null));
    }

    @Override
    public void resetPassword(String email, String newPassword, String token) {
        passwordService.resetPassword(email, newPassword, token);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        passwordService.changePassword(request);
    }
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;
    @Override
    @Transactional
    public BaseTokenResponse getClientToken(UUID clientId, String clientSecret) {
        ServiceClient serviceClient = serviceClientRepo.findByClientIdAndClientSecret(clientId, clientSecret)
                .orElseThrow(() -> new NotFoundException("Service client not found"));
        return DefaultClientTokenResponse.builder()
                .accessToken(jwtService.generateClientToken(serviceClient.getClientId().toString(), serviceClient.getClientHost(),refreshExpiration))
                .tokenType("Bearer")
                .expiresIn(60)
                .build();
    }


}
