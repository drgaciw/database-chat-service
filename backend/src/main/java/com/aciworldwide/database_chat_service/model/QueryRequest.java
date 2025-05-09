package com.aciworldwide.database_chat_service.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a query request from a client.
 * Contains the query string, query type, user role, and optional parameters.
 */
public class QueryRequest {

    @NotBlank(message = "Query cannot be empty")
    private String query;

    @NotNull(message = "Query type must be specified")
    private QueryType queryType;

    @NotNull(message = "User role must be specified")
    private UserRole userRole;

    private String format; // Optional: "json", "table", "chart" (default: "json")
    private Integer limit; // Optional: Maximum number of results (default: 100)
    private String collection; // Optional: Specific collection to query (if known)

    // Getters and setters
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    @Override
    public String toString() {
        return "QueryRequest{" +
                "query='" + query + '\'' +
                ", queryType=" + queryType +
                ", userRole=" + userRole +
                ", format='" + format + '\'' +
                ", limit=" + limit +
                ", collection='" + collection + '\'' +
                '}';
    }
}