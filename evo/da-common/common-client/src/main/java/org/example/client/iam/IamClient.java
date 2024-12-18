package org.example.client.iam;


import org.example.config.FeignClientConfiguration;
import org.example.model.UserAuthority;
import org.example.model.dto.response.BasedResponse;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        url = "http://localhost:8080",
        name = "iam",
        contextId = "da-iam",
        configuration = FeignClientConfiguration.class
        //,fallbackFactory = IamClientFallback.class
)
public interface IamClient {

    @GetMapping("/users/{username}/authorities-by-username")
    @LoadBalanced
    BasedResponse<UserAuthority> getUserAuthority(@PathVariable String username);

    @GetMapping("/users/{clientId}/authorities-by-clientId")
    @LoadBalanced
    BasedResponse<UserAuthority> getClientAuthority(@PathVariable UUID clientId);

    @GetMapping("/auth/client-token/{clientId}/{clientSecret}")
    @LoadBalanced
    BasedResponse<String> getClientToken(@PathVariable String clientId, @PathVariable String clientSecret);

    @GetMapping("/auth/blacklist/{token}")
    @LoadBalanced
    BasedResponse<Boolean> isBlacklisted(@PathVariable String token);


}
