//package org.example.dagateway.config;
//
//import io.github.resilience4j.circuitbreaker.CircuitBreaker;
//import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class CircuitBreakerLoggingFilter implements GlobalFilter {
//
//    private final CircuitBreakerRegistry circuitBreakerRegistry;
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("iamCircuitBreaker");
//        String path = exchange.getRequest().getPath().value();
//
//        // Log trạng thái hiện tại
//        CircuitBreaker.State state = circuitBreaker.getState();
//        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
//
//        log.error("Request to: {} - Circuit Breaker State: {}", path, state);
//        log.error("Metrics for {}", path);
//        log.error("- Failure Rate: {}%", metrics.getFailureRate());
//        log.error("- Failed Calls: {}", metrics.getNumberOfFailedCalls());
//        log.error("- Successful Calls: {}", metrics.getNumberOfSuccessfulCalls());
//        log.error("- Not Permitted Calls: {}", metrics.getNumberOfNotPermittedCalls());
//
//        // Thêm event listener để theo dõi chuyển đổi trạng thái
//        circuitBreaker.getEventPublisher()
//                .onStateTransition(event -> {
//                    log.error("Circuit Breaker State Changed: {} -> {}",
//                            event.getStateTransition().getFromState(),
//                            event.getStateTransition().getToState());
//                });
//
//        return chain.filter(exchange);
//    }
//}
