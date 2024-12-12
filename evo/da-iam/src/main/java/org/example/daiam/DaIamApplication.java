package org.example.daiam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "org.example.daiam.repo")//tai sao khong co cai nay trong micro thi project khong quet duoc repo folder
@EnableAspectJAutoProxy
@SpringBootApplication(scanBasePackages = {"org.example.daiam", "org.example.client", "org.example.web"})
public class DaIamApplication {

    public static void main(String[] args) {
        SpringApplication.run(DaIamApplication.class, args);
    }
}
