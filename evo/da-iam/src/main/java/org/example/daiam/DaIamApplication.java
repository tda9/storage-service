package org.example.daiam;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableFeignClients
@EnableJpaRepositories(basePackages = {"org.example.daiam.infrastruture.persistence.repository","org.example.daiam.repo"})//tai sao khong co cai nay trong micro thi project khong quet duoc repo folder
@EnableAspectJAutoProxy
@SpringBootApplication
@EnableAsync
@EnableDiscoveryClient
//@OpenAPIDefinition(info = @Info(title = "Iam API", version = "1.0", description = "Documentation Iam API v1.0"))
public class DaIamApplication {

    public static void main(String[] args) {
        SpringApplication.run(DaIamApplication.class, args);
    }
}
