package com.aciworldwide.database_chat_service.model;

/**
 * Enum representing types of queries that can be submitted.
 * - NATURAL: Natural language queries (e.g., "What are the top 5 products by sales in Q1 2025?")
 * - STRUCTURED: Structured MongoDB queries (e.g., "db.sales.find({ "quarter": "Q1-2025" }).sort({ "total": -1 }).limit(5)")
 */
public enum QueryType {
    NATURAL,
    STRUCTURED
}