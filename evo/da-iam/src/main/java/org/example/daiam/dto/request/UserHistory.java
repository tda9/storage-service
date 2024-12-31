package org.example.daiam.dto.request;

import java.util.UUID;

public record UserHistory(
        UUID userId,
        String email,
        String username
) {

}
