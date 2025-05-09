package com.aciworldwide.database_chat_service.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Service Registry Application.
 * This application runs a Eureka server for service discovery.
 */
@SpringBootApplication
// @EnableEurekaServer
public class ServiceRegistryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceRegistryApplication.class, args);
    }
}