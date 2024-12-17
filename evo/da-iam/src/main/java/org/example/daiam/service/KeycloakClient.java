package org.example.daiam.service;

import org.example.daiam.dto.response.KeycloakClientTokenResponse;
import org.example.daiam.dto.response.KeycloakAccessTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        url = "${application.security.keycloak.serverUrl}",
        name = "keycloak",
        contextId = "keycloak"
)
public interface KeycloakClient {
    @PostMapping(value = "${application.security.keycloak.logoutUrl}",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<?> logout(@RequestBody MultiValueMap<String, String> body);

    @PostMapping(value = "${application.security.keycloak.newAccessTokenUrl}",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<KeycloakAccessTokenResponse> refreshToken(@RequestBody MultiValueMap<String, String> body);

    @PostMapping(value = "${application.security.keycloak.newAccessTokenUrl}",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<KeycloakAccessTokenResponse> getKeycloakUserToken(@RequestBody MultiValueMap<String, String> body);
    @PostMapping(value = "${application.security.keycloak.newAccessTokenUrl}",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<KeycloakClientTokenResponse> getKeycloakClientToken(@RequestBody MultiValueMap<String, Object> body);

}
