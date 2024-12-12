package org.example.daiam.service;

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

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class PasswordService {
    private final UserRepo userRepo;
    private final EmailService emailService;
    private final PasswordResetTokenRepo passwordResetTokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final BlackListTokenRepo blackListTokenRepo;

    public void changePassword(String currentPassword, String newPassword, String confirmPassword, String email) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }

//    public void forgotPassword(String to) {
//        User user = userRepo.findByEmail(to).orElseThrow();
//        Optional<PasswordResetToken> lastToken = passwordResetTokenRepo.findTopByUserIdOrderByCreatedDateDesc(user.getUserId());
//        if (lastToken.isPresent() && lastToken.get().getExpirationDate().isAfter(LocalDateTime.now())) {
//            throw new TooManyRequestsException("You can only request a password reset every 5 minutes.");
//        } else if (lastToken.isPresent() && lastToken.get().getExpirationDate().isBefore(LocalDateTime.now())) {
//            passwordResetTokenRepo.delete(lastToken.get());
//        }
//        String token = generateToken();
//        //emailService.sendEmail(to, "Confirm password reset", "Your token is:"+ token);
//        emailService.sendEmailWithHtmlTemplate(to, "Confirm password reset", "ResetPasswordTemplate.html", token);
//        passwordResetTokenRepo.save(new PasswordResetToken(token, LocalDateTime.now().plusMinutes(5), user.getUserId()));
//    }

    public void forgotPassword(String to) {
        User user = userRepo.findByEmail(to).orElseThrow();
        Optional<PasswordResetToken> lastToken = passwordResetTokenRepo.findTopByUserIdOrderByCreatedDateDesc(user.getUserId());
        if (lastToken.isPresent() && lastToken.get().getExpirationDate().isAfter(LocalDateTime.now())) {
            throw new TooManyRequestsException("You can only request a password reset every 5 minutes.");
        } else if (lastToken.isPresent() && lastToken.get().getExpirationDate().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepo.delete(lastToken.get());
        }
        String token = generateToken();
        emailService.sendEmail(to, "Confirm password reset", "Your token is:"+ token);
        //emailService.sendResetPassword(to, "Confirm password reset", "ResetPasswordTemplate.html", token);
        passwordResetTokenRepo.save(new PasswordResetToken(token, LocalDateTime.now().plusMinutes(5), user.getUserId()));
    }
    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    public void resetPassword(String email, String newPassword, String token) {
        User user = userRepo.findByEmail(email).orElseThrow();
        PasswordResetToken requestToken = passwordResetTokenRepo.findPasswordResetTokenByToken(token);
        if (requestToken.getExpirationDate().isBefore(LocalDateTime.now()) || !Objects.equals(user.getUserId(), requestToken.getUserId())) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        passwordResetTokenRepo.delete(requestToken);
    }
    public void confirmRegisterEmail(String email, String token) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
        BlackListToken requestToken = blackListTokenRepo.findByToken(token)
                .orElseThrow(()->new IllegalArgumentException("Invalid token"));
        if (requestToken.getExpirationDate().isBefore(LocalDateTime.now()) || !Objects.equals(user.getUserId(), requestToken.getUserId())) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
        user.setVerified(true);
        userRepo.save(user);
        blackListTokenRepo.delete(requestToken);
    }

}
