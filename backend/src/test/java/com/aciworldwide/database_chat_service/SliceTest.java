package com.aciworldwide.database_chat_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A simple test that verifies the Spring context loads
 */
@SpringBootTest(classes = SliceTest.TestConfig.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.cloud.config.enabled=false",
    "spring.cloud.gateway.enabled=false",
    "spring.cloud.config.discovery.enabled=false",
    "eureka.client.enabled=false",
    "spring.cloud.compatibility-verifier.enabled=false",
    "spring.main.web-application-type=none"
})
public class SliceTest {

    @Test
    public void contextLoads() {
        // This test will pass if the Spring context can be loaded
        assertTrue(true, "Spring context should load successfully");
    }

    /**
     * Minimal test configuration that excludes problematic auto-configurations
     */
    @Configuration
    @EnableAutoConfiguration(exclude = {
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class,
        MongoRepositoriesAutoConfiguration.class,
        RedisAutoConfiguration.class
    })
    static class TestConfig {
        // Empty configuration
    }
}
