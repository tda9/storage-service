package org.example.daiam.service.impl;

import org.example.daiam.dto.request.LoginRequest;
import org.example.daiam.dto.request.LogoutRequest;
import org.example.daiam.dto.request.RegisterRequest;
import org.example.daiam.dto.response.BaseTokenResponse;
import org.example.daiam.dto.response.BasedResponse;
import org.example.daiam.dto.response.DefaultTokenResponse;
import org.example.daiam.entity.User;
import org.example.daiam.exception.ErrorResponseException;
import org.example.daiam.exception.UserNotFoundException;




import lombok.extern.slf4j.Slf4j;
import org.example.daiam.repo.BlackListTokenRepo;
import org.example.daiam.repo.RoleRepo;
import org.example.daiam.repo.UserRepo;
import org.example.daiam.repo.UserRoleRepo;
import org.example.daiam.service.*;
import org.example.model.UserAuthority;
import org.example.web.security.AuthorityService;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class AuthenticationService extends BaseService implements BaseAuthenticationService{
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserRoleRepo userRoleRepo;
    private final AuthenticationManager authenticationManager;
    private final PasswordService passwordService;

    public AuthenticationService(UserRepo userRepo,
                                 RoleRepo roleRepo,
                                 PasswordEncoder passwordEncoder,
                                 UserService userService, EmailService emailService,
                                 UserRoleRepo userRoleRepo,
                                 JWTService jwtService,
                                 BlackListTokenRepo blackListTokenRepo,
                                 AuthenticationManager authenticationManager, PasswordService passwordService) {
        super(userRepo, roleRepo,blackListTokenRepo,jwtService);
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.userRoleRepo = userRoleRepo;
        this.authenticationManager = authenticationManager;
        this.passwordService = passwordService;
    }

    @Override
    @Transactional
    public User register(RegisterRequest request) {
        try {
        checkEmailExisted(request.email());
        List<UUID> rolesId = getRoles(request.role());
        User newUser = User.builder()//khoi tao user
                .email(request.email())
                .username(request.username())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phone(request.phone())
                .dob(request.dob())
                .password(passwordEncoder.encode(request.password()))
                .build();
            User user = userRepo.save(newUser);
            UUID newUserId = userRepo.getUserIdByEmail(request.email())
                    .orElseThrow(() -> new ErrorResponseException("Internal error during save user id to user_role table"));
            rolesId.forEach(roleId -> userRoleRepo.saveUserRole(newUserId, roleId));
            DefaultTokenResponse tokenResponse = generateDefaultToken(request.email(), newUserId);
            //5 phut hieu luc, trong thoi gian do khong duoc gui them
            emailService.sendConfirmationRegistrationEmail(request.email(), tokenResponse.getAccessToken());
            log.info("Register new user: " + user + " " + LocalDateTime.now());
            return user;
        } catch (Exception e) {
            log.error("Error at register " + LocalDateTime.now());
            throw new ErrorResponseException("Register failed: " + e.getMessage());
        }
    }



    @Override
    public BaseTokenResponse login(LoginRequest request) {
        String email = request.email();
        String password = request.password();
        User userEntity = userRepo.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException("User not found during login"));
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        return generateDefaultToken(email, userEntity.getUserId());
    }


    @Override
    public BaseTokenResponse refreshToken(String refreshToken) {
        try {
            //UUID id = blackListTokenRepo.findByToken(accessToken).orElseThrow(() -> new IllegalArgumentException("Invalid access token")).getUserId();
            //accesss token moi nhat va con han
//            if (blackListTokenRepo.findTopByUserIdOrderByCreatedDateDesc(id).isPresent()
//                    && jwtService.isTokenValid(accessToken, CustomUserDetails.builder().email(userDetails.getEmail()).build())) {
//                return BasedResponse.builder()
//                        .requestStatus(true)
//                        .httpStatusCode(200)
//                        .message("Access token is still valid")
//                        .data(accessToken)
//                        .build();
//            } else if (blackListTokenRepo.findTopByUserIdOrderByCreatedDateDesc(id).isPresent()
            //&& !jwtService.isTokenValid(accessToken, CustomUserDetails.builder().email(userDetails.getEmail()).build())) {
            if (jwtService.isRefreshTokenValid(refreshToken)) {
                String email = jwtService.extractEmail(refreshToken);
                UUID id = userRepo.findByEmail(email).orElseThrow().getUserId();
                return generateDefaultToken(email, id);
            }
            throw new IllegalArgumentException("Invalid refresh token");
        } catch (Exception e) {
            throw new ErrorResponseException("Invalid refresh token: " + e.getMessage());
        }
    }

    @Transactional//tim hieu tai sao o day can transactional
    public void logout(LogoutRequest request) {
        String email = request.email();
        User u = userRepo.findByEmail(email).orElseThrow();
        blackListTokenRepo.deleteAllByUserId(u.getUserId());
        BasedResponse.success("Logout success", email);
    }

    @Override
    public void resetPassword(String email, String newPassword, String token) {
        passwordService.resetPassword(email, newPassword, token);
    }

    @Override
    public void changePassword(String currentPassword, String newPassword, String confirmPassword, String email) {
        passwordService.changePassword(currentPassword, newPassword, confirmPassword, email);
    }


}
