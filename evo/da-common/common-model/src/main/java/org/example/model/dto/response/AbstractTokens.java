package org.example.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractTokens {
    @JsonProperty("access_token")
    protected String accessToken;
    @JsonProperty("expires_in")
    protected Integer expiresIn;
    @JsonProperty("token_type")
    protected String tokenType;
    protected String scope;
}
