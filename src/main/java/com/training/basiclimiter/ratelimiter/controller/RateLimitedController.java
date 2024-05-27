package com.training.basiclimiter.ratelimiter.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@RestController
@Slf4j
@RequestMapping("/ratelimited")
public class RateLimitedController {

    @Autowired
    private RestTemplate restTemplate;

    private static final String BASE_URL = "http://localhost:8081/virtualthread/question";

    private static final String SERVICE = "rateLimitedService";

    int count = 1;

    @PostMapping(value = "/questiontoai")
    @CircuitBreaker(name = SERVICE, fallbackMethod = "serviceFallback")
    @Retry(name = SERVICE)
    @RateLimiter(name = SERVICE)
    public String askQuestionToVirtualThread(
            @RequestBody String question
    ) {
        log.info("Retry method called " + count++ + " times at " + new Date());

        return restTemplate.postForObject(
                BASE_URL,
                question,
                String.class
        );
    }

    public String serviceFallback(Exception e) {
        return "This is a fallback method for Service";
    }
}
