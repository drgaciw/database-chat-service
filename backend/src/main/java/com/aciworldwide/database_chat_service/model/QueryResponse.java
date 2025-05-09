package com.aciworldwide.database_chat_service.model;

import lombok.Builder;
import lombok.Data;
import org.bson.Document;

import java.util.List;
import java.util.Map;

/**
 * Represents a response to a query request.
 * Contains the query results, metadata, and optional visualization data.
 */
@Data
@Builder
public class QueryResponse {

    /**
     * Status of the query execution ("success" or "error").
     */
    private String status;

    /**
     * The MongoDB query that was executed.
     */
    private String executedQuery;

    /**
     * Array of results or formatted data.
     */
    private List<Document> results;

    /**
     * Optional metadata about the results (count, totalCount, executionTime, etc.).
     */
    private Map<String, Object> metadata;

    /**
     * Optional visualization data if format is "chart".
     */
    private Map<String, Object> visualization;

    /**
     * Error message if status is "error".
     */
    private String error;
}