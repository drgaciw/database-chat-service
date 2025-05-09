package com.aciworldwide.database_chat_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A basic test class with minimal Spring configuration
 */
@SpringBootTest(
    classes = BasicTest.MinimalConfig.class,
    properties = {
        "spring.main.web-application-type=none",
        "spring.cloud.config.enabled=false",
        "spring.cloud.gateway.enabled=false",
        "spring.cloud.config.discovery.enabled=false",
        "spring.ai.openai.enabled=false",
        "spring.ai.azure.openai.enabled=false",
        "spring.ai.anthropic.enabled=false",
        "spring.cloud.compatibility-verifier.enabled=false"
    }
)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class BasicTest {

    @Test
    public void basicTest() {
        assertTrue(true, "This test should always pass");
    }

    @Configuration
    @EnableAutoConfiguration(exclude = {
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class,
        MongoRepositoriesAutoConfiguration.class,
        RedisAutoConfiguration.class,
        DataSourceAutoConfiguration.class
    })
    static class MinimalConfig {
        // Empty configuration
    }
}
