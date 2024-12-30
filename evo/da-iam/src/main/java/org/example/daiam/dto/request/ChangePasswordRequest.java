package org.example.daiam.dto.request;

import org.example.daiam.utils.InputUtils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

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
