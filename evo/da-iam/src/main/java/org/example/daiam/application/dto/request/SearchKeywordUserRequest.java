package org.example.daiam.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.model.dto.request.PagingRequest;


@EqualsAndHashCode(callSuper = true)
@Data
//tai sao ko co cai nay gay ro loi, tim hieu lai moi quan he ke thua trong builder va superbuilder
@AllArgsConstructor
@NoArgsConstructor
public class SearchKeywordUserRequest extends PagingRequest {
    @NotBlank(message = "Keyword cannot be blank")
    String keyword;
}