package org.example.daiam.service;


import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.example.daiam.application.dto.request.ChangePasswordRequest;
import org.example.daiam.entity.BlackListToken;
import org.example.daiam.entity.PasswordResetToken;
import org.example.daiam.exception.TooManyRequestsException;
import org.example.daiam.exception.UserNotFoundException;
import org.example.daiam.infrastruture.persistence.entity.UserEntity;
import org.example.daiam.infrastruture.persistence.repository.UserEntityRepository;
import org.example.daiam.repo.BlackListTokenRepo;
import org.example.daiam.repo.PasswordResetTokenRepo;
import lombok.RequiredArgsConstructor;
import org.example.web.support.RedisService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class PasswordService {
    private final UserEntityRepository userEntityRepository;
    private final EmailService emailService;
    private final PasswordResetTokenRepo passwordResetTokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final BlackListTokenRepo blackListTokenRepo;
    private final RedisService redisService;
    private final JWTService jwtService;

    public void changePassword(ChangePasswordRequest request) {
        String email = request.email();
        String currentPassword = request.currentPassword();
        String newPassword = request.newPassword();
        String confirmPassword = request.confirmPassword();

        UserEntity entity = userEntityRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found to change password"));
        if (!newPassword.equals(confirmPassword)) {
            throw new BadRequestException("Passwords do not match");
        }
        if (!passwordEncoder.matches(currentPassword, entity.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        entity.setPassword(passwordEncoder.encode(newPassword));
        userEntityRepository.save(entity);
    }
    public void forgotPassword(String to) {
        UserEntity user = userEntityRepository.findByEmail(to).orElseThrow();
        emailSpamHandler(user);
        String token = generateToken();
        emailService.sendEmail(to, "Confirm password reset", "Your token is:"+ token);
        //emailService.sendResetPassword(to, "Confirm password reset", "ResetPasswordTemplate.html", token);
        passwordResetTokenRepo.save(new PasswordResetToken(token, LocalDateTime.now().plusMinutes(5), user.getUserId()));
    }
    public void emailSpamHandler(UserEntity user){
        Optional<PasswordResetToken> lastToken = passwordResetTokenRepo.findTopByUserIdOrderByCreatedDateDesc(user.getUserId());
        if (lastToken.isPresent() && lastToken.get().getExpirationDate().isAfter(LocalDateTime.now())) {
            throw new TooManyRequestsException("You can only request a password reset every 5 minutes.");
        } else if (lastToken.isPresent() && lastToken.get().getExpirationDate().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepo.delete(lastToken.get());
        }
    }
    public String generateToken() {
        return UUID.randomUUID().toString();
    }
    public void resetPassword(String email, String newPassword, String token) {
        //TODO: check format password here
        UserEntity user = userEntityRepository.findByEmail(email).orElseThrow();
        PasswordResetToken requestToken = passwordResetTokenRepo.findPasswordResetTokenByToken(token);
        if (requestToken.getExpirationDate().isBefore(LocalDateTime.now()) || !Objects.equals(user.getUserId(), requestToken.getUserId())) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userEntityRepository.save(user);
        passwordResetTokenRepo.delete(requestToken);
    }

    @Transactional
    public void verifyEmail(String email, String token) {
        //during this method, what if power lost or database connection lost,
        // what is fail-safe approach for this
        UserEntity user = userEntityRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Email to verify not found"));
        if(!redisService.isEntryExist(token)){
            throw new NotFoundException("Email verification in token not existed in database");
        }
        Date expirationDate = jwtService.extractExpiration(token);
        if (expirationDate.before(new Date())) {
            throw new BadRequestException("Invalid or expired token");
        }
        user.setVerified(true);
        userEntityRepository.save(user);
    }

}
