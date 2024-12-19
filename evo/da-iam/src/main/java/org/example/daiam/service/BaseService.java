package org.example.daiam.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.example.daiam.dto.response.DefaultAccessTokenResponse;
import org.example.daiam.entity.User;
import org.example.daiam.repo.BlackListTokenRepo;
import org.example.daiam.repo.RoleRepo;
import org.example.daiam.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.web.support.RedisService;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseService {
    protected final UserRepo userRepo;
    protected final RoleRepo roleRepo;
    protected final JWTService jwtService;
    protected final RedisService redisService;

    protected void checkExistedEmail(String email) {
        if (userRepo.existsByEmail(email)) {
            throw new BadRequestException("Email existed");
        }
    }
    protected User getUser(String email, String msg) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(msg));
    }
    protected UUID getUserId(String email, String msg) {
        return  userRepo.getUserIdByEmail(email)
                .orElseThrow(() -> new NotFoundException(msg));
    }
    protected List<UUID> getRoles(Set<String> requestRoles) {
        return requestRoles.stream()
                .map(String::trim)
                .map(roleRepo::findRoleIdByName)
                .peek(role -> {
                    if (role.isEmpty() || roleRepo.isRoleDeleted(role.get()).orElseThrow()) {
                        throw new NotFoundException("Role not found or deleted");}
                })
                .map(Optional::get)
                .toList();
    }

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;


    protected DefaultAccessTokenResponse generateDefaultToken(String email, UUID userId) {
        var jwtToken = jwtService.generateToken(email,jwtExpiration);
        var jwtRefreshToken = jwtService.generateRefreshToken(email);
        return new DefaultAccessTokenResponse(jwtToken, jwtRefreshToken, "Bearer", jwtExpiration, refreshExpiration);
    }

    protected void saveBlackAccessToken(HttpServletRequest servletRequest) {
        if (servletRequest != null && servletRequest.getHeader("Authorization") != null) {
            String token = servletRequest.getHeader("Authorization").substring(7);
            redisService.save(token);
        }
    }
    protected void saveBlackRefreshToken(HttpServletRequest servletRequest,String refreshToken) {
        if(redisService.isEntryExist(refreshToken)){
            throw new BadRequestException("Forbidden refresh token");
        }
        if (servletRequest != null && servletRequest.getHeader("Authorization") != null) {
            String accessToken = servletRequest.getHeader("Authorization").substring(7);
            redisService.save(accessToken);
        }
        redisService.save(refreshToken);
    }
}
