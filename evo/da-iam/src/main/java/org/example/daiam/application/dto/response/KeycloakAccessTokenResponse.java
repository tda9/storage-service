//package org.example.daiam.application.dto.response;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.example.model.dto.response.AbstractTokens;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class KeycloakAccessTokenResponse implements AbstractTokens {
//    @JsonProperty("access_token")
//    private String accessToken;
//    @JsonProperty("refresh_token")
//    private String refreshToken;
//    @JsonProperty("token_type")
//    private String tokenType;
//    @JsonProperty("expires_in")
//    private int expiresIn;
//    @JsonProperty("refresh_expires_in")
//    private int refreshExpiresIn;
//    @JsonProperty("id_token")
//    private String idToken;
//    @JsonProperty("scope")
//    private String scope;
//}