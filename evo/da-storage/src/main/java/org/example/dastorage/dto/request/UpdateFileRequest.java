package org.example.dastorage.dto.request;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
public class UpdateFileRequest {
    @NotNull
    private Boolean deleted ;
    @NotNull
    private String fileName;
    @NotNull
    private Boolean isPublic;
    @NotNull
    private UUID userId;
}
