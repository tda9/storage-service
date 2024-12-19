package org.example.dastorage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
        //(scanBasePackages = {"org.example.dastorage", "org.example.client", "org.example.web"})
public class DaStorageApplication {

    public static void main(String[] args) {
        SpringApplication.run(DaStorageApplication.class, args);
    }

}
