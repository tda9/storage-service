package org.example.daiam.application.service.others;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import lombok.extern.slf4j.Slf4j;
import org.example.daiam.application.dto.AccessTokens;
import org.example.daiam.infrastruture.persistence.repository.UserEntityRepository;
import org.example.web.support.MessageUtils;
import org.example.daiam.utils.RSAKeyUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.example.web.support.RedisService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;
    private final UserEntityRepository userEntityRepository;
    private final RedisService redisService;

    public String generateRefreshToken(String username) {
        PrivateKey privateKey = rsaKeyUtil.getPrivateKey();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public String generateClientToken(String client_id, String clientHost, long jwtExpiration) {
        PrivateKey privateKey = rsaKeyUtil.getPrivateKey();
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .claim("preferred_email", clientHost)
                .claim("clientAddress", clientHost)
                .claim("client_id", client_id)
                .claim("clientHost", clientHost)
                .setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public String generateToken(String email, long jwtExpiration) {
        PrivateKey privateKey = rsaKeyUtil.getPrivateKey();
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(email)
                .claim("preferred_email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    private final RSAKeyUtil rsaKeyUtil;

    public String extractEmail(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isRefreshTokenValid(String token) {
        String email = extractEmail(token);
        if (email == null) {
            log.info("Email field in refresh token is missing");
            throw new InvalidBearerTokenException(MessageUtils.INVALID_REFRESH_TOKEN_MESSAGE);
        }
        if (!userEntityRepository.existsByEmail(email)) {
            log.info("Email in refresh token not existed in database");
            throw new InvalidBearerTokenException(MessageUtils.INVALID_REFRESH_TOKEN_MESSAGE);
        }
        return !isTokenExpired(token);
    }

    public String extractId(String jwt) {
        return extractClaim(jwt, Claims::getId);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            PublicKey publicKey = rsaKeyUtil.getPublicKey();
            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public AccessTokens generateAccessTokens(String email) {
        var jwtToken = generateToken(email, jwtExpiration);
        var jwtRefreshToken = generateRefreshToken(email);
        return AccessTokens.builder()
                .accessToken(jwtToken)
                .refreshToken(jwtRefreshToken)
                .tokenType("Bearer")
                .idToken(extractId(jwtToken))
                .refreshExpiresIn(refreshExpiration)
                .build();
    }

    public void saveBlackAccessToken(HttpServletRequest servletRequest) {
        if (servletRequest != null && servletRequest.getHeader("Authorization") != null) {
            String token = servletRequest.getHeader("Authorization").substring(7);
            redisService.save(token);
        }
    }

    public void saveBlackRefreshToken(HttpServletRequest servletRequest, String refreshToken) {
        if (redisService.isEntryExist(refreshToken)) {
            throw new ForbiddenException(MessageUtils.FORBIDDEN_REFRESH_TOKEN_MESSAGE);
        }
        saveBlackAccessToken(servletRequest);
        redisService.save(refreshToken);
    }
}
