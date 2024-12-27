package org.example.daiam.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SearchUserRequest (
        @NotBlank(message = "Missing keyword")
        String keyword){
}
// de tren param