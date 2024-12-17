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
                                 BlackListTokenRepo blackListTokenRepo,
                                 PasswordService passwordService,
                                 ServiceClientRepo serviceClientRepo) {
        super(userRepo, roleRepo, blackListTokenRepo, jwtService);
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
        //5 phut hieu luc, trong thoi gian do khong duoc gui them
        //emailService.verifyEmail(request.email(), tokenResponse.getAccessToken());
        return user;
    }

    @Override
    public BaseTokenResponse login(LoginRequest request, HttpServletRequest servletRequest) {
        User userEntity = userRepo.findByEmail(request.email())
                .orElseThrow(() -> new NotFoundException("Login email not found"));
        checkValidUser(userEntity);
        if (servletRequest != null && servletRequest.getHeader("Authorization") != null) {
            saveInvalidToken(servletRequest, userEntity.getUserId());
        }
        return generateDefaultToken(userEntity.getEmail(), userEntity.getUserId());
    }

    private void checkValidUser(User user) {
        if (!user.isVerified()) {
            passwordService.emailSpamHandler(user);
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
    public BaseTokenResponse refreshToken(String refreshToken) {
        if (jwtService.isRefreshTokenValid(refreshToken)) {
            String email = jwtService.extractEmail(refreshToken);
            UUID id = userRepo.getUserIdByEmail(email)
                    .orElseThrow(() -> new NotFoundException("User not found"));
            return generateDefaultToken(email, id);
        }
        throw new BadRequestException("Invalid refresh token");
    }

    @Transactional//tim hieu tai sao o day can transactional
    public ResponseEntity<?> logout(LogoutRequest request, HttpServletRequest servletRequest) {
        String email = request.email();
        UUID id = userRepo.getUserIdByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        saveInvalidToken(servletRequest, id);
        return ResponseEntity.ok(BasedResponse.success("Logout successful", email));
    }

    @Transactional
    protected void saveInvalidToken(HttpServletRequest servletRequest, UUID id) {
        String accessToken = servletRequest.getHeader("Authorization").substring(7);
        blackListTokenRepo.save(BlackListToken.builder()
                .token(accessToken)
                .expirationDate(jwtService.extractExpiration(accessToken))
                .userId(id)
                .tokenId(UUID.fromString(jwtService.extractId(accessToken)))
                .build());
    }

    @Override
    public void resetPassword(String email, String newPassword, String token) {
        passwordService.resetPassword(email, newPassword, token);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        passwordService.changePassword(request);
    }

    @Override
    @Transactional
    public BaseTokenResponse getClientToken(UUID clientId, String clientSecret) {
        ServiceClient serviceClient = serviceClientRepo.findByClientIdAndClientSecret(clientId, clientSecret)
                .orElseThrow(() -> new NotFoundException("Service client not found"));
        return DefaultClientTokenResponse.builder()
                .accessToken(jwtService.generateClientToken(serviceClient.getClientId().toString(), serviceClient.getClientHost()))
                .tokenType("Bearer")
                .expiresIn(60)
                .build();
    }


}
