package org.example.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.example.model.dto.response.ClientTokens;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
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
        String token = fetchNewToken();
        if (token != null && !token.isEmpty()) {
            requestTemplate.header("Authorization", "Bearer " + token);
        }
    }

    private String fetchNewToken() {
        String tokenUrl = "http://localhost:8081/api/auth/client/token/" + client_id + "/" + client_secret;
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<ClientTokens> response = restTemplate.getForEntity(
                    tokenUrl,
                    ClientTokens.class);
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



