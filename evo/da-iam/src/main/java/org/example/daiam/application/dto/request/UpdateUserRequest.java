package org.example.daiam.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.daiam.utils.InputUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;

public record UpdateUserRequest(
        @NotBlank(message = "Update email cannot be empty")
        @Pattern(regexp = InputUtils.EMAIL_FORMAT, message = InputUtils.INVALID_EMAIL_PATTERN_MESSAGE)
        String email,
        @NotBlank(message = "Username cannot be empty")
        String username,
        @Pattern(regexp = InputUtils.PASSWORD_PATTERN, message = "Invalid update password format")
        String password,
        Set<String> roleNames,
        Boolean isRoot,
        Boolean isLock,
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
        @Pattern(regexp = InputUtils.PHONE_PATTERN, message = "Invalid update phone number format")
        String phone,
        String street,
        String ward,
        String province,
        String district
) {}
