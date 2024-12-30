package org.example.daiam.application.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.example.daiam.utils.InputUtils;

public record ChangePasswordRequest(
        @Pattern(regexp = InputUtils.PASSWORD_PATTERN, message = "Invalid password format")
        String currentPassword,
        @Pattern(regexp = InputUtils.PASSWORD_PATTERN, message = "Invalid password format")
        String newPassword,
        @Pattern(regexp = InputUtils.PASSWORD_PATTERN, message = "Invalid password format")
        String confirmPassword,
        @NotEmpty(message = "Change password mail cannot be empty")
        @Pattern(regexp = InputUtils.EMAIL_FORMAT, message = "Invalid email format")
        String email
) {
}
