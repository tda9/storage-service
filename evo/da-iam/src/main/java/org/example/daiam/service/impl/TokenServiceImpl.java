package org.example.daiam.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.daiam.entity.BlackListToken;
import org.example.daiam.repo.BlackListTokenRepo;
import org.example.daiam.service.JWTService;
import org.example.web.security.TokenCacheService;
import org.example.web.support.RedisService;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
