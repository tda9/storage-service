package org.example.config;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@Configuration
//@EnableAsync
public class FeignClientConfiguration {

    @Bean
    public FeignClientInterceptor requestInterceptor() {
        return new FeignClientInterceptor();
    }
    @Bean
    public Encoder feignFormEncoder() {
        return new SpringFormEncoder();
    }
}
