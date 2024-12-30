package org.example.daiam.application.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.BadRequestException;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.daiam.application.dto.response.DefaultAccessTokenResponse;
import org.example.daiam.service.JWTService;
import org.example.web.support.RedisService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class CommonService {
    public final JWTService jwtService;
    protected final RedisService redisService;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;


    public DefaultAccessTokenResponse generateDefaultToken(String email, UUID userId) {
        var jwtToken = jwtService.generateToken(email,jwtExpiration);
        var jwtRefreshToken = jwtService.generateRefreshToken(email);
//        LocalDateTime localDateTime = LocalDateTime.now().plusDays(1);
//        Date expirationDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        //redisService.save(jwtToken);
        return new DefaultAccessTokenResponse(jwtToken, jwtRefreshToken, "Bearer", jwtExpiration, refreshExpiration);
    }

    public void saveBlackAccessToken(HttpServletRequest servletRequest) {
        if (servletRequest != null && servletRequest.getHeader("Authorization") != null) {
            String token = servletRequest.getHeader("Authorization").substring(7);
            redisService.save(token);
        }
    }
    public void saveBlackRefreshToken(HttpServletRequest servletRequest, String refreshToken) {
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
