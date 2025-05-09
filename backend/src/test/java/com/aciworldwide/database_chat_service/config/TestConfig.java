package com.aciworldwide.database_chat_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TestConfig {

    @Bean
    @Primary
    public RestTemplate mockRestTemplate() {
        return new RestTemplate();
    }
}