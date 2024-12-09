package com.da.dto.request;

import jakarta.persistence.Column;
import lombok.Builder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.UUID;

public record SearchFileRequest (
     boolean deleted,
     String fileType,
     Long size,
     String fileName,
     String filePath,
     UUID userId,
     int version,
     LocalDateTime createdDate,
     LocalDateTime lastModifiedDate,
     boolean isPublic){
}
