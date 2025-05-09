package com.aciworldwide.database_chat_service.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a user in the system.
 * Used for authentication and authorization.
 */
@Getter
@Setter
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    private String password;

    private Set<UserRole> roles = new HashSet<>();
}