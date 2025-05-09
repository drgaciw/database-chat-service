package com.aciworldwide.database_chat_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * A minimal Spring test that only loads a test configuration
 */
@SpringBootTest(
    classes = MinimalSpringTest.TestConfig.class,
    properties = {
        "spring.main.web-application-type=none",
        "spring.cloud.config.enabled=false",
        "spring.cloud.gateway.enabled=false",
        "spring.cloud.config.discovery.enabled=false",
        "spring.cloud.compatibility-verifier.enabled=false"
    }
)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class MinimalSpringTest {

    @Autowired
    private ApplicationContext context;

    @Test
    public void contextLoads() {
        // Verify that the application context loads successfully
        assertNotNull(context, "Application context should not be null");
    }

    /**
     * Minimal test configuration
     */
    @Configuration
    @EnableAutoConfiguration(exclude = {
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class,
        MongoRepositoriesAutoConfiguration.class,
        RedisAutoConfiguration.class,
        DataSourceAutoConfiguration.class
    })
    static class TestConfig {
        // Empty configuration class
    }
}
