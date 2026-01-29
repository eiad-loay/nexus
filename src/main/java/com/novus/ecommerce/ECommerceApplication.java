package com.novus.ecommerce;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableCaching
@Slf4j
public class ECommerceApplication {

    static void main(String[] args) {
        log.info("Starting ECommerceApplication");
        SpringApplication.run(ECommerceApplication.class, args);
        log.info("ECommerceApplication started");
    }

}
