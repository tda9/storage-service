package org.example.daiam.application.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import org.example.daiam.application.dto.DefaultCredentials;
import org.example.daiam.application.dto.request.ChangePasswordRequest;
import org.example.daiam.application.dto.request.LoginRequest;
import org.example.daiam.application.dto.request.LogoutRequest;
import org.example.daiam.application.request_command_mapper.UserRequestAndCommandMapper;
import org.example.daiam.application.service.*;
import org.example.daiam.application.service.others.CommonService;
import org.example.daiam.application.service.others.EmailService;
import org.example.daiam.application.service.others.JwtService;
import org.example.daiam.application.service.others.PasswordService;
import org.example.daiam.domain.User;
import org.example.daiam.domain.command.CreateUserCommand;

import org.example.daiam.application.dto.request.RegisterRequest;
import org.example.daiam.infrastruture.domainrepository.impl.RoleDomainRepositoryImpl;
import org.example.daiam.infrastruture.domainrepository.impl.UserDomainRepositoryImpl;
import org.example.daiam.infrastruture.persistence.entity.ClientEntity;
import org.example.daiam.infrastruture.persistence.repository.ClientEntityRepository;
import org.example.daiam.infrastruture.persistence.repository.UserEntityRepository;
import org.example.model.dto.response.ClientTokens;
import org.example.web.support.MessageUtils;
import org.example.model.dto.response.AbstractTokens;

import lombok.extern.slf4j.Slf4j;
import org.example.model.dto.response.Response;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Slf4j
@Service("defaultAuthenticationServiceImpl")
@Primary
public class AuthenticationServiceImpl implements AuthenticationService {
    private final RoleDomainRepositoryImpl roleDomainRepositoryImpl;
    private final UserDomainRepositoryImpl userDomainRepositoryImpl;
    private final PasswordEncoder passwordEncoder;
    private final PasswordService passwordService;
    private final UserRequestAndCommandMapper userRequestAndCommandMapper;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final UserEntityRepository userEntityRepository;
    private final CommonService commonService;
private final ClientEntityRepository clientEntityRepository;
    public AuthenticationServiceImpl(RoleDomainRepositoryImpl roleDomainRepositoryImpl,
                                     UserDomainRepositoryImpl userDomainRepositoryImpl,
                                     PasswordEncoder passwordEncoder,
                                     PasswordService passwordService,
                                     ClientEntityRepository serviceClientRepo,
                                     UserRequestAndCommandMapper userRequestAndCommandMapper,
                                     UserEntityRepository userEntityRepository,
                                     JwtService jwtService,
                                     EmailService emailService, CommonService commonService, ClientEntityRepository clientEntityRepository
    ) {
        this.roleDomainRepositoryImpl = roleDomainRepositoryImpl;
        this.userDomainRepositoryImpl = userDomainRepositoryImpl;
        this.passwordEncoder = passwordEncoder;
        this.passwordService = passwordService;
        this.userRequestAndCommandMapper = userRequestAndCommandMapper;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.userEntityRepository = userEntityRepository;
        this.commonService = commonService;
        this.clientEntityRepository = clientEntityRepository;
    }


    @Override
    @Transactional
    public User register(RegisterRequest request) {
        commonService.isExistedEmail(request.email());
        //req to cmd
        CreateUserCommand command = userRequestAndCommandMapper.toCommand(request);
        Set<String> roleNames = request.roleNames();
        if (!CollectionUtils.isEmpty(roleNames))
            command.setRoleIds(roleDomainRepositoryImpl.getRoleIdsByNames(roleNames));
        command.setPassword(passwordEncoder.encode(request.password()));
        //cmd to domain
        User domain = new User(command);
        domain.setIsRoot(true);
        domain.setIsVerified(true);
        userDomainRepositoryImpl.save(domain);
//        AccessTokens tokenResponse = jwtService.generateAccessTokens(request.email());
//        emailService.verifyEmail(request.email(), tokenResponse.getAccessToken());
        return domain;
    }

    @Override
    public AbstractTokens login(LoginRequest request, HttpServletRequest servletRequest) {
        DefaultCredentials credentials = userEntityRepository.findCredentialByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException(MessageUtils.LOGIN_EMAIL_NOT_FOUND_MESSAGE));
        checkValidUser(credentials);
        jwtService.saveBlackAccessToken(servletRequest);
        return jwtService.generateAccessTokens(request.email());
    }

    //passwordService.emailSpamHandler(user);
    private void checkValidUser(DefaultCredentials credentials) {
        if (!credentials.getIsVerified()) {
            throw new ForbiddenException(MessageUtils.EMAIL_NOT_VERIFIED_MESSAGE);
        }
        if (credentials.getIsLocked()) {
            throw new ForbiddenException(MessageUtils.USER_IS_LOCKED_MESSAGE);
        }
        if (passwordEncoder.matches(credentials.getPassword(), credentials.getPassword())) {
            throw new BadCredentialsException(MessageUtils.INCORRECT_PASSWORD_MESSAGE);
        }
    }

    @Override
    public AbstractTokens refreshToken(String refreshToken, HttpServletRequest servletRequest) {
        if (!jwtService.isRefreshTokenValid(refreshToken)) throw new InvalidBearerTokenException("Invalid refresh token");
        String email = jwtService.extractEmail(refreshToken);
        if (email == null) throw new InvalidBearerTokenException(MessageUtils.INVALID_REFRESH_TOKEN_MESSAGE);
        if(!userEntityRepository.existsByEmail(email)) throw new InvalidBearerTokenException(MessageUtils.EMAIL_NOT_FOUND_MESSAGE);
        jwtService.saveBlackRefreshToken(servletRequest, refreshToken);
        return jwtService.generateAccessTokens(email);


    }

    public ResponseEntity<?> logout(LogoutRequest request, HttpServletRequest servletRequest) {
        jwtService.saveBlackRefreshToken(servletRequest, request.refreshToken());
        return ResponseEntity.ok(Response.success("Logout successful", null));
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
    public AbstractTokens getClientToken(UUID clientId, String clientSecret) {
        ClientEntity entity = clientEntityRepository.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Service client not found"));
        if(!entity.getClientSecret().equals(clientSecret)){
            throw new BadCredentialsException("Invalid client credentials");
        }
        int clientExpireIn = 3600;
        String tokens = jwtService.generateClientToken(entity.getClientId().toString(),entity.getClientHost(),clientExpireIn);
        return ClientTokens.builder()
                .accessToken(tokens)
                .tokenType("Bearer")
                .expiresIn(clientExpireIn)
                .build();

    }


}
