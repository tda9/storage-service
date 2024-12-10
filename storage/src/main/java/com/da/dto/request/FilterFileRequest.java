package com.da.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

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
