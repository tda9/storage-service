package org.example.daregister;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class DaRegisterApplication {

	public static void main(String[] args) {
		SpringApplication.run(DaRegisterApplication.class, args);
	}
	//domain  repo:
	//infrastructure:
}
