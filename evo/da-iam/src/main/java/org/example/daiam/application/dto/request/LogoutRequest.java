package org.example.daiam.application.dto.request;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.example.daiam.utils.InputUtils;

public record LogoutRequest(
        @NotEmpty
        String refreshToken,
        @Pattern(regexp = InputUtils.EMAIL_FORMAT, message = "Invalid logout email format")
        String email) {
}
