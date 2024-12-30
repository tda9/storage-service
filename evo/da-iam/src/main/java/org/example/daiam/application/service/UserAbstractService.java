package org.example.daiam.application.service;

import jakarta.ws.rs.BadRequestException;
import org.example.daiam.infrastruture.persistence.repository.UserEntityRepository;
import org.example.daiam.utils.InputUtils;

import java.util.UUID;

public abstract class UserAbstractService {
    protected final UserEntityRepository userEntityRepository;

    protected UserAbstractService(UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }

    protected void isExistedEmail(String email){
        if (userEntityRepository.existsByEmail(email)) {
            throw new BadRequestException(InputUtils.USER_EMAIL_EXISTED_MESSAGE);
        }
    }

    protected UUID isValidUUID(String uuid) {
        try {
            return UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid UUID");
        }
    }

    protected String generatePassword() {
        return UUID.randomUUID().toString();
    }
}
