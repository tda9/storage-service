package org.example.daiam.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import org.example.web.support.MessageUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;
@Builder
public record UpdateUserRequest(
        @NotBlank(message = "Register email cannot be empty")
        @Pattern(regexp = MessageUtils.EMAIL_FORMAT, message = "Invalid register email format")
        String email,
        @NotBlank(message = "username cannot be null")
        String username,
        String firstName,
        String lastName,
        @DateTimeFormat(pattern = MessageUtils.DOB_PATTERN)
        @Past(message = "Date of birth must be in the past")
        LocalDate dob,
        @Pattern(regexp = MessageUtils.PHONE_PATTERN, message = "Invalid update phone number format")
        String phone,
        @Min(value = 0, message = "stt must equal greater than 0")
        Integer stt,
        @Min(value = 0, message = "experience must equal greater than 0")
        Integer experience,
        Set<String> roles,
        Boolean isVerified,
        Boolean isLock,
        Boolean deleted,
        String image,
        String street,
        String ward,
        String province,
        String district
) {}
