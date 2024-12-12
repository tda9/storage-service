package org.example.client.iam;


import org.example.config.FeignClientConfiguration;
import org.example.model.UserAuthority;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        url = "${app.iam.internal-url:}",
        name = "iam",
        contextId = "common-iam",
        configuration = FeignClientConfiguration.class
        //,fallbackFactory = IamClientFallback.class
)
public interface IamClient {
    @GetMapping("/api/users/{userId}/authorities")
    @LoadBalanced
    ResponseEntity<UserAuthority> getUserAuthority(@PathVariable UUID userId);

    @GetMapping("/api/users/{username}/authorities-by-username")
    @LoadBalanced
    ResponseEntity<UserAuthority> getUserAuthority(@PathVariable String username);
}
