package com.aciworldwide.database_chat_service;

import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.aciworldwide.database_chat_service.repository.UserRepository;
import com.aciworldwide.database_chat_service.repository.HistoryRepository;
import com.aciworldwide.database_chat_service.repository.TemplateRepository;

/**
 * Test configuration to mock dependencies for basic tests
 */
@TestConfiguration
@EnableAutoConfiguration(exclude = {
    MongoAutoConfiguration.class,
    MongoDataAutoConfiguration.class,
    MongoRepositoriesAutoConfiguration.class
})
public class TestConfig {

    @Bean
    public MongoTemplate mongoTemplate() {
        return Mockito.mock(MongoTemplate.class);
    }
    
    @Bean
    public UserRepository userRepository() {
        return Mockito.mock(UserRepository.class);
    }
    
    @Bean
    public HistoryRepository historyRepository() {
        return Mockito.mock(HistoryRepository.class);
    }
    
    @Bean
    public TemplateRepository templateRepository() {
        return Mockito.mock(TemplateRepository.class);
    }
}
