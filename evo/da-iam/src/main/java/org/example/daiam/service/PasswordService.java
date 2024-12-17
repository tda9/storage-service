package org.example.daiam.service;


import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.example.daiam.dto.request.ChangePasswordRequest;
import org.example.daiam.entity.BlackListToken;
import org.example.daiam.entity.PasswordResetToken;
import org.example.daiam.entity.User;
import org.example.daiam.exception.TooManyRequestsException;
import org.example.daiam.exception.UserNotFoundException;
import org.example.daiam.repo.BlackListTokenRepo;
import org.example.daiam.repo.PasswordResetTokenRepo;
import org.example.daiam.repo.UserRepo;
import lombok.RequiredArgsConstructor;
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
    private final UserRepo userRepo;
    private final EmailService emailService;
    private final PasswordResetTokenRepo passwordResetTokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final BlackListTokenRepo blackListTokenRepo;

    public void changePassword(ChangePasswordRequest request) {
        String email = request.email();
        String currentPassword = request.currentPassword();
        String newPassword = request.newPassword();
        String confirmPassword = request.confirmPassword();

        User user = userRepo.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found to change password"));
        if (!newPassword.equals(confirmPassword)) {
            throw new BadRequestException("Passwords do not match");
        }
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }
    public void forgotPassword(String to) {
        User user = userRepo.findByEmail(to).orElseThrow();
        emailSpamHandler(user);
        String token = generateToken();
        emailService.sendEmail(to, "Confirm password reset", "Your token is:"+ token);
        //emailService.sendResetPassword(to, "Confirm password reset", "ResetPasswordTemplate.html", token);
        passwordResetTokenRepo.save(new PasswordResetToken(token, LocalDateTime.now().plusMinutes(5), user.getUserId()));
    }
    public void emailSpamHandler(User user){
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
        User user = userRepo.findByEmail(email).orElseThrow();
        PasswordResetToken requestToken = passwordResetTokenRepo.findPasswordResetTokenByToken(token);
        if (requestToken.getExpirationDate().isBefore(LocalDateTime.now()) || !Objects.equals(user.getUserId(), requestToken.getUserId())) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        passwordResetTokenRepo.delete(requestToken);
    }

    @Transactional
    public void verifyEmail(String email, String token) {
        //during this method, what if power lost or database connection lost,
        // what is fail-safe approach for this
        User user = userRepo.findByEmail(email).orElseThrow(() -> new NotFoundException("Email to verify not found"));
        BlackListToken requestToken = blackListTokenRepo.findByToken(token)
                .orElseThrow(()->new NotFoundException("Email verification token not found"));
        if (requestToken.getExpirationDate().before(new Date()) || !Objects.equals(user.getUserId(), requestToken.getUserId())) {
            throw new BadRequestException("Invalid or expired token");
        }
        user.setVerified(true);
        userRepo.save(user);
        blackListTokenRepo.delete(requestToken);
    }

}
