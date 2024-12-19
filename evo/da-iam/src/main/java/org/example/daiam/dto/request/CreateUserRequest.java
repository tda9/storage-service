package org.example.daiam.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import org.example.daiam.utils.InputUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;

public record CreateUserRequest(
        @NotBlank(message = "Register email cannot be empty")
        @Pattern(regexp = InputUtils.EMAIL_FORMAT, message = "Invalid register email format")
        String email,
        @NotBlank(message = "Username cannot be empty")
        String username,
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
        @Pattern(regexp = InputUtils.PHONE_FORMAT, message = "Invalid create phone number format")
        String phone,
        String street,
        String ward,
        String province,
        String district,
        Set<String> roles){

    //Default:
    //deleted: false, isVerified: false, isLock: false


}
