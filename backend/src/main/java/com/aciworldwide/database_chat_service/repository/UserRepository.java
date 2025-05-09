package com.aciworldwide.database_chat_service.repository;

import com.aciworldwide.database_chat_service.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entities.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    /**
     * Find a user by username.
     *
     * @param username the username to search for
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Check if a user with the given username exists.
     *
     * @param username the username to check
     * @return true if a user with the username exists, false otherwise
     */
    boolean existsByUsername(String username);
}