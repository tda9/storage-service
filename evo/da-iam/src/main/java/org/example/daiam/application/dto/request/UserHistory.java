package org.example.daiam.application.dto.request;

import java.util.UUID;

public record UserHistory(
        UUID userId,
        String email,
        String username
) {

}
