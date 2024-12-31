package org.example.daiam.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.web.security.TokenCacheService;
import org.example.web.support.RedisService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenCacheService {
    private final RedisService redisService;

    @Override
    public void invalidToken(String token) {
    }

    @Override
    public void invalidRefreshToken(String refreshToken) {
    }

    @Override
    public boolean isExisted(String cacheName, String token) {
        return redisService.isEntryExist(token);
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
