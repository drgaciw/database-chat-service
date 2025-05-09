package com.aciworldwide.database_chat_service.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Represents a query history entry.
 * Stores information about executed queries for auditing and history tracking.
 */
@Data
@Document(collection = "query_history")
public class QueryHistory {
    
    @Id
    private String id;
    
    /**
     * Username of the user who executed the query.
     */
    private String username;
    
    /**
     * The original query string.
     */
    private String query;
    
    /**
     * The type of query (NATURAL or STRUCTURED).
     */
    private QueryType queryType;
    
    /**
     * The executed MongoDB query.
     */
    private String executedQuery;
    
    /**
     * Timestamp when the query was executed.
     */
    private Instant timestamp;
    
    /**
     * Status of the query execution ("success" or "error").
     */
    private String status;
    
    /**
     * Error message if status is "error".
     */
    private String errorMessage;
}