package org.example.dagateway.controller;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class CircuitBreakerController {
    @GetMapping("/fallback")
    ResponseEntity<String> customerFallback() {
        return new ResponseEntity<>(
                "We are sorry, but customer service is currently out of service. \nPlease try later",
                HttpStatusCode.valueOf(503));
    }
}
