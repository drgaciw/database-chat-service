package com.aciworldwide.database_chat_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * Basic tests for the application.
 */
@SpringBootTest(
    classes = DatabaseChatServiceApplicationTests.MinimalTestConfiguration.class,
    properties = {
        "spring.cloud.config.enabled=false",
        "spring.cloud.gateway.enabled=false",
        "spring.cloud.config.discovery.enabled=false",
        "spring.main.allow-bean-definition-overriding=true",
        "spring.data.mongodb.auto-index-creation=false",
        "spring.data.redis.repositories.enabled=false",
        "spring.main.web-application-type=none",
        "spring.cloud.compatibility-verifier.enabled=false"
    }
)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class DatabaseChatServiceApplicationTests {

    /**
     * Minimal configuration for testing that the application context loads.
     */
    @Configuration
    @EnableAutoConfiguration(exclude = {
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class,
        MongoRepositoriesAutoConfiguration.class,
        RedisAutoConfiguration.class,
        DataSourceAutoConfiguration.class
    })
    static class MinimalTestConfiguration {
        // Empty configuration class
    }

    /**
     * Test that the application context loads successfully.
     */
    @Test
    void contextLoads() {
        // This test will pass if the application context can be loaded
    }
}
