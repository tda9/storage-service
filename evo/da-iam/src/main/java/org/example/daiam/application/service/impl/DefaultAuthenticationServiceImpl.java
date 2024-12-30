package org.example.daiam.application.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.example.daiam.application.dto.request.ChangePasswordRequest;
import org.example.daiam.application.dto.request.LoginRequest;
import org.example.daiam.application.dto.request.LogoutRequest;
import org.example.daiam.application.dto.response.DefaultAccessTokenResponse;
import org.example.daiam.application.request_command_mapper.UserRequestAndCommandMapper;
import org.example.daiam.application.service.AuthenticationService;
import org.example.daiam.application.service.CommonService;
import org.example.daiam.application.service.UserAbstractService;
import org.example.daiam.domain.User;
import org.example.daiam.domain.command.CreateUserCommand;

import org.example.daiam.application.dto.request.RegisterRequest;
import org.example.daiam.infrastruture.domainrepository.RoleDomainRepositoryImpl;
import org.example.daiam.infrastruture.domainrepository.UserDomainRepositoryImpl;
import org.example.daiam.infrastruture.persistence.entity.UserEntity;
import org.example.daiam.infrastruture.persistence.repository.ClientEntityRepository;
import org.example.daiam.infrastruture.persistence.repository.UserEntityRepository;
import org.example.model.dto.response.BaseTokenResponse;

import lombok.extern.slf4j.Slf4j;
import org.example.daiam.service.*;
import org.example.model.dto.response.BasedResponse;
import org.example.web.support.RedisService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
public class DefaultAuthenticationServiceImpl
        extends UserAbstractService
        implements AuthenticationService {
    private final RoleDomainRepositoryImpl roleDomainRepositoryImpl;
    private final UserDomainRepositoryImpl userDomainRepositoryImpl;
    private final PasswordEncoder passwordEncoder;
    private final PasswordService passwordService;
    private final UserRequestAndCommandMapper userRequestAndCommandMapper;
    private final CommonService commonService;
    private final EmailService emailService;

    public DefaultAuthenticationServiceImpl(RoleDomainRepositoryImpl roleDomainRepositoryImpl,
                                            UserDomainRepositoryImpl userDomainRepositoryImpl,
                                            PasswordEncoder passwordEncoder,
                                            PasswordService passwordService,
                                            ClientEntityRepository serviceClientRepo,
                                            UserRequestAndCommandMapper userRequestAndCommandMapper,
                                            UserEntityRepository userEntityRepository,
                                            CommonService commonService,
                                            JWTService jwtService,
                                            RedisService redisService, EmailService emailService
    ) {
        super(userEntityRepository);
        this.roleDomainRepositoryImpl = roleDomainRepositoryImpl;
        this.userDomainRepositoryImpl = userDomainRepositoryImpl;
        this.passwordEncoder = passwordEncoder;
        this.passwordService = passwordService;
        this.userRequestAndCommandMapper = userRequestAndCommandMapper;
        this.commonService = commonService;
        this.emailService = emailService;
    }


    @Override
    @Transactional
    public User register(RegisterRequest request) {
        isExistedEmail(request.email());
        //req to cmd
        CreateUserCommand command = userRequestAndCommandMapper.toCommand(request);
        Set<String> roleNames = request.roleNames();
        if (!CollectionUtils.isEmpty(roleNames)) {
            command.setRoleIds(roleDomainRepositoryImpl.getRoleIdsByNames(roleNames));
        }
        command.setPassword(passwordEncoder.encode(request.password()));
        command.setIsVerified(true);
        command.setIsRoot(true);
        //cmd to domain
        User domain = new User(command);
        userDomainRepositoryImpl.save(domain);
        //DefaultAccessTokenResponse tokenResponse = commonService.generateDefaultToken(request.email(), domain.getUserId());
        //emailService.verifyEmail(request.email(), tokenResponse.getAccessToken());
        return domain;
    }

    @Override
    public BaseTokenResponse login(LoginRequest request, HttpServletRequest servletRequest) {
        UserEntity entity = userEntityRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestException("Login email not found"));
        checkValidUser(entity);
        commonService.saveBlackAccessToken(servletRequest);//TODO: save refresh token to redis after the first time
        return commonService.generateDefaultToken(entity.getEmail(), entity.getUserId());
    }

    private void checkValidUser(UserEntity entity) {
        if (!entity.isVerified()) {
            //passwordService.emailSpamHandler(user);
            throw new BadRequestException("Email not verified");
        } else if (entity.isLock()) {
            throw new BadRequestException("User is locked");
        } else if (entity.isDeleted()) {
            throw new BadRequestException("User was deleted");
        } else if (passwordEncoder.matches(entity.getPassword(), entity.getPassword())) {
            throw new BadRequestException("Incorrect password");
        }
    }

    @Override
    public BaseTokenResponse refreshToken(String refreshToken, HttpServletRequest servletRequest) {
        if (commonService.jwtService.isRefreshTokenValid(refreshToken)) {
            String email = commonService.jwtService.extractEmail(refreshToken);
            UserEntity entity = userEntityRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found in refresh token"));
            commonService.saveBlackRefreshToken(servletRequest, refreshToken);
            return commonService.generateDefaultToken(entity.getEmail(), entity.getUserId());
        }
        throw new BadRequestException("Invalid refresh token");
    }

    public ResponseEntity<?> logout(LogoutRequest request, HttpServletRequest servletRequest) {
        commonService.saveBlackRefreshToken(servletRequest, request.refreshToken());
        return ResponseEntity.ok(BasedResponse.success("Logout successful", null));
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
//        ClientEntity entity = serviceClientRepo.findByClientIdAndClientSecret(clientId, clientSecret)
//                .orElseThrow(() -> new NotFoundException("Service client not found"));
//        return DefaultClientTokenResponse.builder()
//                .accessToken(commonService.jwtService.generateClientToken(serviceClient.getClientId().toString(), serviceClient.getClientHost(), refreshExpiration))
//                .tokenType("Bearer")
//                .expiresIn(60)
//                .build();
        return null;
    }


}
