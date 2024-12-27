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

@Builder
@Getter
@Setter
public class UpdateUserRequest {

    @NotBlank(message = "Update email cannot be empty")
    @Pattern(regexp = InputUtils.EMAIL_FORMAT, message = "Invalid update email format")
    private String email;
    @NotBlank(message = "Username cannot be empty")
    private String username;
    @Pattern(regexp = InputUtils.PASSWORD_FORMAT, message = "Invalid update password format")
    private String password;
    private Set<String> roleNames;
    private Boolean isRoot;
    private Boolean isLock;
    private Boolean isVerified;
    @Min(value = 0, message = "stt must equal greater than 0")
    private Integer stt;
    @Min(value = 0, message = "experience must equal greater than 0")
    private Integer experience;
    private String firstName;
    private String lastName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dob;
    @Pattern(regexp = InputUtils.PHONE_FORMAT, message = "Invalid update phone number format")
    private String phone;
    private String street;
    private String ward;
    private String province;
    private String district;
}
