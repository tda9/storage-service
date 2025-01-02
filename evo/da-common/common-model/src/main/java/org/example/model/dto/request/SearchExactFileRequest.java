package org.example.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchExactFileRequest extends PagingRequest{
    private String fileType;
    private Long size;
    private String fileName;
    private String filePath;
    private String userId;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
