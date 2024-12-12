package org.example.dastorage.dto.request;

import java.time.LocalDateTime;

public record FilterFileRequest(
     String fileType,
     Long size,
     String fileName,
     String filePath,
     String userId,
     LocalDateTime createdDate,
     LocalDateTime lastModifiedDate
){
}
