package org.example.daiam.application.dto.request;

import jakarta.validation.constraints.*;
import org.example.web.support.MessageUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;

public record UpdateUserRequest(
        @NotBlank(message = "Update email cannot be empty")
        @Pattern(regexp = MessageUtils.EMAIL_FORMAT, message = MessageUtils.INVALID_EMAIL_PATTERN_MESSAGE)
        String email,
        @NotBlank(message = "Username cannot be empty")
        String username,
        @NotBlank(message = "Password cannot be empty")
        @Pattern(regexp = MessageUtils.PASSWORD_PATTERN, message = "Invalid update password format")
        String password,
        Set<String> roleNames,
        @NotNull(message = "Root status cannot be null")
        Boolean isRoot,
        @NotNull(message = "Lock status cannot be null")
        Boolean isLock,
        @NotNull(message = "Verify status cannot be null")
        Boolean isVerified,
        @Min(value = 0, message = "stt must equal greater than 0")
        Integer stt,
        @Min(value = 0, message = "experience must equal greater than 0")
        Integer experience,
        String firstName,
        String lastName,
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @Past(message = "Date of birth must be in the past")
        LocalDate dob,
        @Pattern(regexp = MessageUtils.PHONE_PATTERN, message = "Invalid update phone number format")
        String phone,
        String street,
        String ward,
        String province,
        String district
) {}
