package org.example.dagateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class RetryLoggingGatewayFilterFactory extends AbstractGatewayFilterFactory<RetryLoggingGatewayFilterFactory.Config> {

    public RetryLoggingGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Log the initial request
            logRetryAttempt("Initial request", exchange);

            // Create a retry filter and add logging
            return chain.filter(exchange).doOnError(error -> {
                // Log the retry error
                logRetryAttempt("Retry failed", exchange);
            }).onErrorResume(error -> {
                // Handle error and apply retry logic here, or call fallback
                logRetryAttempt("Retry attempt failed, applying fallback", exchange);
                return Mono.empty();
            });
        };
    }

    // Helper method to log retry attempts
    private void logRetryAttempt(String message, ServerWebExchange exchange) {
        HttpMethod method = exchange.getRequest().getMethod();
        String uri = exchange.getRequest().getURI().toString();
        System.out.println(String.format("%s for %s %s", message, method, uri));
    }

    public static class Config {
        // Configurable properties for your custom retry behavior can go here
    }
}