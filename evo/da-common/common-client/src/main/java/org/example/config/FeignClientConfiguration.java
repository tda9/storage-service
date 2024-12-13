package org.example.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class FeignClientConfiguration {

    @Bean
    public FeignClientInterceptor requestInterceptor() {
        return new FeignClientInterceptor();
    }


}
