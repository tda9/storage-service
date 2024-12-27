package org.example.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.example.model.dto.response.BaseTokenResponse;
import org.example.model.dto.response.ClientTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.task.TaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Value("${spring.application.client-id}")
    private String client_id;
    @Value("${spring.application.client-secret}")
    private String client_secret;


    //    @Override
//    public void apply(RequestTemplate requestTemplate) {
//        String token = getClientToken();
//        if (token != null && !token.isEmpty()) {
//            requestTemplate.header("Authorization", "Bearer " + token);
//        }
//    }
//
//    private String getClientToken() {
//        String tokenUrl = "http://localhost:8080/auth/client-token/"+client_id+"/"+client_secret;
//        RestTemplate restTemplate = new RestTemplate();
//        try {
//            ResponseEntity<ClientTokenResponse> response = restTemplate.getForEntity(
//                    tokenUrl,
//                    ClientTokenResponse.class);
//            if (response.getStatusCode().is2xxSuccessful()) {
//                return Objects.requireNonNull(response.getBody()).getAccessToken(); // Assuming the token is the plain response body
//            } else {
//                throw new RuntimeException("Failed to retrieve token. Status: " + response.getStatusCode());
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("Error while fetching client token: " + e.getMessage(), e);
//        }
//    }
    @Override
    @CircuitBreaker(name = "userAuthorityService")
    @Retry(name = "userAuthorityService")
    @TimeLimiter(name = "userAuthorityService")
    @Bulkhead(name = "userAuthorityService")
    @RateLimiter(name = "userAuthorityService")
    public void apply(RequestTemplate requestTemplate) {
        try {
            // Blocking call to wait for the token to be fetched
            String token = getClientToken().get();  // This will block until the token is retrieved
            if (token != null && !token.isEmpty()) {
                // Apply the token to the request
                requestTemplate.header("Authorization", "Bearer " + token);
            } else {
                throw new RuntimeException("Token is null or empty.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to apply token: " + e.getMessage(), e);
        }
    }

    @Autowired
    private TaskExecutor customTaskExecutor;
    //distributed , jobLauncher
    private CompletableFuture<String> getClientToken() {
        return CompletableFuture.supplyAsync(() -> {
                            String tokenUrl = "http://localhost:8080/auth/client-token/" + client_id + "/" + client_secret;
                            RestTemplate restTemplate = new RestTemplate();
                            try {
                                ResponseEntity<ClientTokenResponse> response = restTemplate.getForEntity(
                                        tokenUrl,
                                        ClientTokenResponse.class
                                );
                                if (response.getStatusCode().is2xxSuccessful()) {
                                    return Objects.requireNonNull(response.getBody()).getAccessToken(); // Assuming the response has a `getToken` method
                                } else {
                                    throw new RuntimeException("Failed to retrieve token. Status: " + response.getStatusCode());
                                }
                            } catch (Exception e) {
                                throw new RuntimeException("Error while fetching client token: " + e.getMessage(), e);
                            }
                        }
                        , customTaskExecutor
                )
                .exceptionally(e -> {
                    throw new RuntimeException("Token request timed out: " + e.getMessage(), e);
                });
    }

}
