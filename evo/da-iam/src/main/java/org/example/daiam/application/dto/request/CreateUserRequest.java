package org.example.daiam.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import org.example.web.support.MessageUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;

public record CreateUserRequest(
        @NotBlank(message = MessageUtils.BLANK_EMAIL_MESSAGE)
        @Pattern(regexp = MessageUtils.EMAIL_FORMAT,
                message = MessageUtils.INVALID_EMAIL_PATTERN_MESSAGE)
        String email,
        @NotBlank(message = MessageUtils.BLANK_USERNAME_MESSAGE)
        String username,
        Set<String> roleNames,
        Boolean isRoot,
        Boolean isLock,
        Boolean isVerified,
        @Min(value = 0, message = "stt must equal or greater than 0")
        Integer stt,
        @Min(value = 0, message = "experience must equal or greater than 0")
        Integer experience,
        String firstName,
        String lastName,
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @Past(message = "Date of birth must be in the past")
        LocalDate dob,
        @Pattern(regexp = MessageUtils.PHONE_PATTERN, message = "Invalid create phone number format")
        String phone,
        String street,
        String ward,
        String province,
        String district
) { }
