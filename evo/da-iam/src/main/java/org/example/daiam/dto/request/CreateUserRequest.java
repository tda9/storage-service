package org.example.daiam.dto.request;

import jakarta.validation.constraints.*;
import org.example.web.support.MessageUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;

public record CreateUserRequest(
        @NotBlank(message = "Register email cannot be empty")
        @Pattern(regexp = MessageUtils.EMAIL_FORMAT, message = "Invalid register email format")
        String email,
        @NotBlank(message = "Username cannot be empty")
        // @Pattern(regexp = InputUtils.USERNAME_FORMAT, message = "Invalid register username format")
        String username,

        //Default: false
        Boolean isRoot,
        Boolean isLock,
        Boolean isVerified,

        @Min(value = 0, message = "Stt must equal greater than 0")
        Integer stt,
        @Min(value = 0, message = "Experience must equal greater than 0")
        Integer experience,

        String firstName,
        String lastName,

        //TODO: Check case -2021-09-29
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @Past(message = "Date of birth must be in the past")
        LocalDate dob,

        @Pattern(regexp = MessageUtils.PHONE_PATTERN, message = "Invalid create phone number format")
        String phone,
        String street,
        String ward,
        String province,
        String district,

        Set<String> roles) {
}
