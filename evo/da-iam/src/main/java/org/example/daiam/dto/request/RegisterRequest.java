package org.example.daiam.dto.request;

import jakarta.validation.constraints.*;
import org.example.web.support.MessageUtils;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;


@Builder
public record RegisterRequest(
        @NotBlank(message = "Register email cannot be empty")
        @Pattern(regexp = MessageUtils.EMAIL_FORMAT, message = "Invalid register email format")
        String email,
        @NotBlank(message = "Register password cannot be empty")
        @Pattern(regexp = MessageUtils.PASSWORD_PATTERN, message = "Invalid password format")
        String password,
        @NotBlank(message = "Username cannot be empty")
        String username,
        @Min(value = 0, message = "stt must equal or greater than 0")
        Integer experience,

        String firstName,
        String lastName,
        @DateTimeFormat(pattern = "yyyy-MM-dd")//TODO: handle case -2023-02-02
        @Past(message = "Date of birth must be in the past")
        LocalDate dob,
        @Pattern(regexp = MessageUtils.PHONE_PATTERN, message = "Invalid register phone number format")
        String phone,
        String street,
        String ward,
        String province,
        String district,
        Set<String> roleNames) {// does spring set hash same string request?
}
