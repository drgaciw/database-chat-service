package com.aciworldwide.database_chat_service.repository;

import com.aciworldwide.database_chat_service.model.QueryTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for QueryTemplate entities.
 */
@Repository
public interface TemplateRepository extends MongoRepository<QueryTemplate, String> {
    
    /**
     * Find query templates by username.
     *
     * @param username the username to search for
     * @return a list of QueryTemplate entries
     */
    List<QueryTemplate> findByUsername(String username);
    
    /**
     * Find query templates by name (case-insensitive, partial match).
     *
     * @param name the name to search for
     * @return a list of QueryTemplate entries
     */
    List<QueryTemplate> findByNameContainingIgnoreCase(String name);
}