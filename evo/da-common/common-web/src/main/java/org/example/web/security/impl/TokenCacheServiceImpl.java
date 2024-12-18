package org.example.web.security.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.client.iam.IamClient;
import org.example.web.security.TokenCacheService;
import org.example.web.support.RedisService;
import org.springframework.stereotype.Service;
@RequiredArgsConstructor
@Service
@Slf4j
public class TokenCacheServiceImpl implements TokenCacheService {
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
}
