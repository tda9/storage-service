package org.example.daiam.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.daiam.entity.BlackListToken;
import org.example.daiam.repo.BlackListTokenRepo;
import org.example.daiam.service.JWTService;
import org.example.web.security.TokenCacheService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenCacheService {
    private final BlackListTokenRepo blackListTokenRepo;
private final JWTService jwtService;
    @Override
    public void invalidToken(String token) {
        blackListTokenRepo.save(BlackListToken.builder()
                        .token(token)
                .build());
    }

    @Override
    public void invalidRefreshToken(String refreshToken) {

    }

    @Override
    public boolean isExisted(String cacheName, String token) {
        return blackListTokenRepo.existsById(UUID.fromString(jwtService.extractId(token)));
    }

    @Override
    public boolean isInvalidToken(String token) {
        return isExisted(null, token);
    }

    @Override
    public boolean isInvalidRefreshToken(String refreshToken) {
        return TokenCacheService.super.isInvalidRefreshToken(refreshToken);
    }
}
