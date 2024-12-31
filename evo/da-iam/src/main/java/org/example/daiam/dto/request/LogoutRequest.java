package org.example.daiam.dto.request;


import org.example.web.support.MessageUtils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public record LogoutRequest(
        @NotEmpty
        String refreshToken,
        @Pattern(regexp = MessageUtils.EMAIL_FORMAT, message = "Invalid logout email format")
        String email) {
}
