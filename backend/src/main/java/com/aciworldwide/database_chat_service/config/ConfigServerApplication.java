package com.aciworldwide.database_chat_service.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Config Server Application.
 * This application runs a Spring Cloud Config Server for centralized configuration.
 */
@SpringBootApplication
// @EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}