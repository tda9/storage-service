package org.example.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientTokens extends AbstractTokens {
    @JsonProperty("not_before_policy")
    private String notBeforePolicy;
}
