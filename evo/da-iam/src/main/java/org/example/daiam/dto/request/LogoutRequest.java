package org.example.daiam.dto.request;


import org.example.daiam.utils.InputUtils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public record LogoutRequest(
        @NotEmpty
        String refreshToken,
        @Pattern(regexp = InputUtils.EMAIL_FORMAT, message = "Invalid logout email format")
        String email) {
}
