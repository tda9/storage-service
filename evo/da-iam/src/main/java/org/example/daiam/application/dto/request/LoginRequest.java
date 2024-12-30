package org.example.daiam.application.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.example.daiam.utils.InputUtils;


@Builder
public record LoginRequest(
        @NotEmpty
        @Pattern(regexp = InputUtils.EMAIL_FORMAT, message = "Invalid login email format")
        String email,
        @NotEmpty
        @Pattern(regexp = InputUtils.PASSWORD_PATTERN, message = "Invalid login password format")
        String password) {
}
