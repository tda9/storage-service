package org.example.daiam.dto.request;

import org.example.web.support.MessageUtils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;


@Builder
public record LoginRequest(
        @NotEmpty
        @Pattern(regexp = MessageUtils.EMAIL_FORMAT, message = "Invalid login email format")
        String email,
        @NotEmpty
        @Pattern(regexp = MessageUtils.PASSWORD_PATTERN, message = "Invalid login password format")
        String password) {
}
