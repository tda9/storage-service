package org.example.model.dto.request;

import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagingRequest {
    private static final String ASC_SYMBOL = "asc";
    private static final String DESC_SYMBOL = "desc";
    @Min(value = 1, message = "Page index must be greater than 0")
    @Builder.Default
    protected int currentPage = 1;
    @Builder.Default
    @Min(value = 0, message = "Page size must be greater or equal than 0")
    protected int pageSize = 0;
    protected String sortBy;
    @Builder.Default
    protected String sort = ASC_SYMBOL;
}
