package com.aciworldwide.database_chat_service.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

/**
 * Configuration for Redis caching.
 * Enables caching and configures Redis as the cache provider.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configure Redis cache manager.
     *
     * @param connectionFactory the Redis connection factory
     * @return the Redis cache manager
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer()));
        
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfig)
                .withCacheConfiguration("queryResults", 
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(5)))
                .withCacheConfiguration("databaseMetadata", 
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofHours(1)))
                .build();
    }
}