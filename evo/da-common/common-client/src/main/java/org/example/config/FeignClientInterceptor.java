package org.example.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.example.model.dto.response.BaseTokenResponse;
import org.example.model.dto.response.ClientTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Slf4j
@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Value("${spring.application.client-id}")
    private String client_id;
    @Value("${spring.application.client-secret}")
    private String client_secret;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String token = getClientToken();
        if (token != null && !token.isEmpty()) {
            requestTemplate.header("Authorization", "Bearer " + token);
        }
    }

    private String getClientToken() {
        String tokenUrl = "http://localhost:8080/auth/client-token/"+client_id+"/"+client_secret;
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<ClientTokenResponse> response = restTemplate.getForEntity(
                    tokenUrl,
                    ClientTokenResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return Objects.requireNonNull(response.getBody()).getAccessToken(); // Assuming the token is the plain response body
            } else {
                throw new RuntimeException("Failed to retrieve token. Status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching client token: " + e.getMessage(), e);
        }
    }

}
