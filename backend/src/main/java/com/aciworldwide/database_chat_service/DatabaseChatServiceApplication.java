package com.aciworldwide.database_chat_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Main application class for the MongoDB Chat Query Microservice.
 * This microservice enables users to "chat" with a MongoDB database by submitting
 * natural language queries or structured queries.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class DatabaseChatServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatabaseChatServiceApplication.class, args);
    }
}
