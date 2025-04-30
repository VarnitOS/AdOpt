package com.adopt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AdoptApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdoptApplication.class, args);
    }
} 