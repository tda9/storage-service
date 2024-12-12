package org.example.daiam.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record SearchUserRequest (
        @NotBlank(message = "Missing keyword")
        String keyword){
}
// de tren param