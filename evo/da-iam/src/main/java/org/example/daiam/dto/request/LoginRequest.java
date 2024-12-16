package org.example.daiam.dto.request;

import org.example.daiam.utils.InputUtils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;


@Builder
public record LoginRequest(
        @NotEmpty
        @Pattern(regexp = InputUtils.EMAIL_FORMAT, message = "Invalid login email format")
        String email,
        @NotEmpty
        @Pattern(regexp = InputUtils.PASSWORD_FORMAT, message = "Invalid login password format")
        String password) {
}
