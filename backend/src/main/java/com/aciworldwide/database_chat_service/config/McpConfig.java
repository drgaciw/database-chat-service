package com.aciworldwide.database_chat_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration for MongoDB Chat MCP Server integration
 */
@Configuration
public class McpConfig {

    @Value("${mcp.server.url:http://localhost:3003}")
    private String mcpServerUrl;

    @Bean
    public RestTemplate mcpRestTemplate() {
        return new RestTemplate();
    }
    
    @Bean
    public WebClient mcpWebClient() {
        return WebClient.builder()
            .baseUrl(mcpServerUrl)
            .build();
    }
}
