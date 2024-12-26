package org.example.dagateway.config;


import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CentralSwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()))
                .info(new Info()
                        .title("DA-GATEWAY API")
                        //.description("Some custom description of API.")
                        .version("1.0")
                        .contact(new Contact().name("Tran Duc Anh")
                                .email("tducanh157@gmail.com")
                                .url("/iam/auth/login"))
                        .license(new License().name("License of API").url("API license URL")));
    }


    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }
    @Value("${springdoc.api-docs.group}")
    private String group;
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group(group)
                .pathsToMatch("/**")
                .build();
    }
    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    private TimeLimiterRegistry timeLimiterRegistry;
    @Bean
    public Resilience4JCircuitBreakerFactory resilience4JCircuitBreakerFactory() {
        Resilience4JCircuitBreakerFactory resilience4JCircuitBreakerFactory =
                new Resilience4JCircuitBreakerFactory(circuitBreakerRegistry, timeLimiterRegistry, null);
        resilience4JCircuitBreakerFactory.configureDefault(this::createResilience4JCircuitBreakerConfiguration);
        return resilience4JCircuitBreakerFactory;
    }

    private Resilience4JConfigBuilder.Resilience4JCircuitBreakerConfiguration createResilience4JCircuitBreakerConfiguration(String id) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(id);
        CircuitBreakerConfig circuitBreakerConfig = circuitBreaker.getCircuitBreakerConfig();
        TimeLimiterConfig timeLimiterConfig = timeLimiterRegistry.timeLimiter(id)
                .getTimeLimiterConfig();
        circuitBreaker.getEventPublisher()
                .onEvent(event -> System.out.println("Circuit-breaker Event Publisher : " + event));
        return new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(circuitBreakerConfig)
                .timeLimiterConfig(timeLimiterConfig)
                .build();
    }
}
