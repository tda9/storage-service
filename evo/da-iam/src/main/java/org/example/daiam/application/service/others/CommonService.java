package org.example.daiam.application.service.others;

import jakarta.ws.rs.BadRequestException;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.daiam.infrastruture.persistence.repository.UserEntityRepository;
import org.example.web.support.MessageUtils;
import org.example.web.support.RedisService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class CommonService {
    public final JwtService jwtService;
    protected final RedisService redisService;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;


    public final UserEntityRepository userEntityRepository;

    public void isExistedEmail(String email) {
        if (userEntityRepository.existsByEmail(email)) {
            throw new BadRequestException(MessageUtils.USER_EMAIL_EXISTED_MESSAGE);
        }
    }

    public UUID isValidUUID(String uuid) {
        try {
            return UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid UUID");
        }
    }

    public String generatePassword() {
        return UUID.randomUUID().toString();
    }
}
