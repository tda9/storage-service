package org.example;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"org.example.client","org.example.web"})
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}