package com.aciworldwide.database_chat_service.service;

import com.aciworldwide.database_chat_service.model.*;
import com.aciworldwide.database_chat_service.nlp.NlpProcessor;
import com.aciworldwide.database_chat_service.repository.HistoryRepository;
import com.aciworldwide.database_chat_service.repository.TemplateRepository;
import com.mongodb.client.MongoDatabase;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

/**
 * Service for processing database queries.
 * Handles both natural language and structured queries.
 */
@Service
public class QueryService {

    private static final Logger logger = LoggerFactory.getLogger(QueryService.class);
    private static final Set<String> BUSINESS_ACCESSIBLE_COLLECTIONS = Set.of("sales", "products", "customers", "orders");

    private final MongoTemplate mongoTemplate;
    private final NlpProcessor nlpProcessor;
    private final HistoryRepository historyRepository;
    private final TemplateRepository templateRepository;

    public QueryService(
            MongoTemplate mongoTemplate,
            NlpProcessor nlpProcessor,
            HistoryRepository historyRepository,
            TemplateRepository templateRepository) {
        this.mongoTemplate = mongoTemplate;
        this.nlpProcessor = nlpProcessor;
        this.historyRepository = historyRepository;
        this.templateRepository = templateRepository;
    }

    /**
     * Process a query request and return the results.
     *
     * @param request The query request
     * @return The query response
     */
    @CircuitBreaker(name = "mongoQueryCircuitBreaker", fallbackMethod = "fallbackProcessQuery")
    @Cacheable(value = "queryResults", key = "#request.query + '-' + #request.collection + '-' + #request.userRole")
    public QueryResponse processQuery(QueryRequest request) {
        logger.info("Processing query: {}", request.getQuery());
        
        // Check if user has access to the requested collection
        if (request.getCollection() != null && !hasAccessToCollection(request.getCollection(), request.getUserRole())) {
            throw new AccessDeniedException("Access denied to collection: " + request.getCollection());
        }
        
        long startTime = System.currentTimeMillis();
        String executedQuery;
        List<Document> results;
        
        try {
            // Process the query based on its type
            if (request.getQueryType() == QueryType.NATURAL) {
                // Process natural language query
                executedQuery = processNaturalLanguageQuery(request);
            } else {
                // Process structured query
                validateStructuredQuery(request.getQuery());
                executedQuery = request.getQuery();
            }
            
            // Execute the query
            results = executeQuery(executedQuery, request.getCollection());
            
            // Save query to history
            saveQueryToHistory(request, executedQuery, true, null);
            
            // Format the results based on user role and requested format
            return formatResults(results, request, System.currentTimeMillis() - startTime);
            
        } catch (Exception e) {
            logger.error("Error processing query", e);
            
            // Save failed query to history
            saveQueryToHistory(request, null, false, e.getMessage());
            
            // Return error response
            return QueryResponse.builder()
                    .status("error")
                    .error(e.getMessage())
                    .build();
        }
    }
    
    /**
     * Fallback method for processQuery when the circuit breaker is open.
     *
     * @param request The query request
     * @param e The exception that triggered the fallback
     * @return The fallback query response
     */
    public QueryResponse fallbackProcessQuery(QueryRequest request, Exception e) {
        logger.warn("Circuit breaker triggered for query: {}", request.getQuery(), e);
        
        // Save failed query to history
        saveQueryToHistory(request, null, false, "Service temporarily unavailable: " + e.getMessage());
        
        return QueryResponse.builder()
                .status("error")
                .error("Service temporarily unavailable. Please try again later.")
                .build();
    }

    /**
     * Process a natural language query and convert it to a MongoDB query.
     *
     * @param request The query request
     * @return The MongoDB query
     */
    private String processNaturalLanguageQuery(QueryRequest request) {
        // Use the NLP processor to translate natural language to MongoDB query
        return nlpProcessor.processQuery(
                request.getQuery(),
                request.getUserRole(),
                request.getCollection()
        );
    }

    /**
     * Validate a structured query to prevent injection attacks.
     *
     * @param query The structured query
     * @throws IllegalArgumentException if the query is invalid
     */
    private void validateStructuredQuery(String query) {
        // Check for prohibited operations
        if (query.contains("dropDatabase") || 
            query.contains("drop(") || 
            query.contains("deleteMany") ||
            query.contains("deleteOne") ||
            query.contains("updateMany") ||
            query.contains("replaceOne")) {
            
            throw new IllegalArgumentException("Prohibited operation detected in query");
        }
        
        // Validate query structure
        try {
            // Parse the query to ensure it's valid
            // This is a simplified validation - in a real implementation, 
            // you would use a more sophisticated approach
            if (!query.startsWith("db.") || !query.contains(".find(") && !query.contains(".aggregate(") && !query.contains(".countDocuments(")) {
                throw new IllegalArgumentException("Invalid query structure");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid query structure: " + e.getMessage());
        }
    }

    /**
     * Execute a MongoDB query and return the results.
     *
     * @param query The MongoDB query
     * @param collection The collection to query (optional)
     * @return The query results
     */
    private List<Document> executeQuery(String query, String collection) {
        // In a real implementation, this would parse and execute the MongoDB query
        // For this sample, we'll return mock results
        
        List<Document> results = new ArrayList<>();
        
        // Mock results for demonstration
        if (query.contains("sales") && query.contains("Q1-2025")) {
            // Mock results for "top 5 products by sales in Q1 2025"
            for (int i = 1; i <= 5; i++) {
                Document doc = new Document();
                doc.append("product", "Product " + i);
                doc.append("total", 1000 - (i * 100));
                doc.append("quarter", "Q1-2025");
                results.add(doc);
            }
        } else if (query.contains("countDocuments") && query.contains("users")) {
            // Mock result for "how many documents in users collection"
            Document doc = new Document();
            doc.append("count", 1250);
            results.add(doc);
        } else {
            // Generic mock results
            for (int i = 1; i <= 3; i++) {
                Document doc = new Document();
                doc.append("id", i);
                doc.append("name", "Sample " + i);
                doc.append("value", i * 10);
                results.add(doc);
            }
        }
        
        return results;
    }

    /**
     * Format the query results based on user role and requested format.
     *
     * @param results The raw query results
     * @param request The original query request
     * @param executionTime The query execution time in milliseconds
     * @return The formatted query response
     */
    private QueryResponse formatResults(List<Document> results, QueryRequest request, long executionTime) {
        // Format results based on user role and requested format
        String format = request.getFormat() != null ? request.getFormat() : "json";
        
        // Build metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("count", results.size());
        metadata.put("executionTime", executionTime);
        
        // For business users, we might want to format the results differently
        if (request.getUserRole() == UserRole.BUSINESS) {
            // For business users, we might add visualization data
            Map<String, Object> visualization = null;
            
            if ("chart".equals(format) && results.size() > 0) {
                visualization = generateVisualizationData(results);
            }
            
            return QueryResponse.builder()
                    .status("success")
                    .executedQuery(request.getQuery())
                    .results(results)
                    .metadata(metadata)
                    .visualization(visualization)
                    .build();
        } else {
            // For admin users, we provide more technical details
            return QueryResponse.builder()
                    .status("success")
                    .executedQuery(request.getQuery())
                    .results(results)
                    .metadata(metadata)
                    .build();
        }
    }

    /**
     * Generate visualization data for chart display.
     *
     * @param results The query results
     * @return The visualization data
     */
    private Map<String, Object> generateVisualizationData(List<Document> results) {
        // In a real implementation, this would analyze the results and generate
        // appropriate visualization data based on the data structure
        
        Map<String, Object> visualization = new HashMap<>();
        
        // Example for a bar chart
        List<String> labels = new ArrayList<>();
        List<Number> values = new ArrayList<>();
        
        for (Document doc : results) {
            if (doc.containsKey("product") && doc.containsKey("total")) {
                labels.add(doc.getString("product"));
                values.add(doc.getInteger("total"));
            }
        }
        
        visualization.put("type", "bar");
        visualization.put("labels", labels);
        visualization.put("values", values);
        visualization.put("title", "Sales by Product");
        
        return visualization;
    }

    /**
     * Save a query to the history.
     *
     * @param request The query request
     * @param executedQuery The executed query or error message
     * @param success Whether the query was successful
     * @param errorMessage The error message if the query failed
     */
    private void saveQueryToHistory(QueryRequest request, String executedQuery, boolean success, String errorMessage) {
        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // Create history entry
        QueryHistory historyEntry = new QueryHistory();
        historyEntry.setUsername(username);
        historyEntry.setQuery(request.getQuery());
        historyEntry.setQueryType(request.getQueryType());
        historyEntry.setExecutedQuery(executedQuery);
        historyEntry.setTimestamp(Instant.now());
        historyEntry.setStatus(success ? "success" : "error");
        historyEntry.setErrorMessage(errorMessage);
        
        // Save to repository
        historyRepository.save(historyEntry);
    }

    /**
     * Check if a user has access to a collection.
     *
     * @param collection The collection name
     * @param userRole The user role
     * @return True if the user has access, false otherwise
     */
    private boolean hasAccessToCollection(String collection, UserRole userRole) {
        if (userRole == UserRole.ADMIN) {
            // Admins have access to all collections
            return true;
        } else if (userRole == UserRole.BUSINESS) {
            // Business users have access to specific collections
            return BUSINESS_ACCESSIBLE_COLLECTIONS.contains(collection);
        }
        
        return false;
    }

    /**
     * Get database metadata.
     *
     * @param collection Optional collection name
     * @return The database metadata
     */
    @Cacheable(value = "databaseMetadata", key = "#collection != null ? #collection : 'all'")
    public Map<String, Object> getDatabaseMetadata(String collection) {
        logger.info("Fetching database metadata for collection: {}", collection != null ? collection : "all");
        Map<String, Object> metadata = new HashMap<>();
        MongoDatabase db = mongoTemplate.getDb();
        
        metadata.put("database", db.getName());
        
        List<Map<String, Object>> collections = new ArrayList<>();
        
        // If a specific collection is requested
        if (collection != null && !collection.isEmpty()) {
            collections.add(getCollectionMetadata(collection));
        } else {
            // Get metadata for all collections
            for (String collName : db.listCollectionNames()) {
                collections.add(getCollectionMetadata(collName));
            }
        }
        
        metadata.put("collections", collections);
        metadata.put("status", "success");
        
        return metadata;
    }
    
    /**
     * Invalidate database metadata cache.
     * This should be called when the database schema changes.
     */
    @CacheEvict(value = "databaseMetadata", allEntries = true)
    public void invalidateDatabaseMetadataCache() {
        logger.info("Invalidating database metadata cache");
    }

    /**
     * Get metadata for a specific collection.
     *
     * @param collectionName The collection name
     * @return The collection metadata
     */
    private Map<String, Object> getCollectionMetadata(String collectionName) {
        Map<String, Object> metadata = new HashMap<>();
        
        // In a real implementation, this would get actual metadata from MongoDB
        // For this sample, we'll return mock data
        
        metadata.put("name", collectionName);
        metadata.put("count", 1000);
        metadata.put("size", 1024 * 1024);
        
        List<Map<String, Object>> indexes = new ArrayList<>();
        Map<String, Object> index = new HashMap<>();
        index.put("name", "_id_");
        index.put("key", new Document("_id", 1));
        indexes.add(index);
        
        metadata.put("indexes", indexes);
        
        // Sample schema based on collection name
        Map<String, Object> sampleSchema = new HashMap<>();
        
        if ("users".equals(collectionName)) {
            sampleSchema.put("_id", "ObjectId");
            sampleSchema.put("username", "String");
            sampleSchema.put("email", "String");
            sampleSchema.put("createdAt", "Date");
        } else if ("sales".equals(collectionName)) {
            sampleSchema.put("_id", "ObjectId");
            sampleSchema.put("product", "String");
            sampleSchema.put("total", "Number");
            sampleSchema.put("quarter", "String");
            sampleSchema.put("date", "Date");
        } else {
            sampleSchema.put("_id", "ObjectId");
            sampleSchema.put("name", "String");
            sampleSchema.put("value", "Number");
        }
        
        metadata.put("sampleSchema", sampleSchema);
        
        return metadata;
    }

    /**
     * Get query history.
     *
     * @param limit Maximum number of history items
     * @param offset Offset for pagination
     * @return The query history
     */
    public Map<String, Object> getQueryHistory(int limit, int offset) {
        Map<String, Object> response = new HashMap<>();
        
        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // Get history from repository
        Page<QueryHistory> historyPage = historyRepository.findByUsernameOrderByTimestampDesc(
                username, 
                PageRequest.of(offset / limit, limit)
        );
        
        List<Map<String, Object>> history = new ArrayList<>();
        
        for (QueryHistory entry : historyPage.getContent()) {
            Map<String, Object> historyItem = new HashMap<>();
            historyItem.put("id", entry.getId());
            historyItem.put("query", entry.getQuery());
            historyItem.put("queryType", entry.getQueryType().toString());
            historyItem.put("executedQuery", entry.getExecutedQuery());
            historyItem.put("timestamp", entry.getTimestamp());
            historyItem.put("status", entry.getStatus());
            
            history.add(historyItem);
        }
        
        response.put("history", history);
        
        Map<String, Object> pagination = new HashMap<>();
        pagination.put("total", historyPage.getTotalElements());
        pagination.put("limit", limit);
        pagination.put("offset", offset);
        
        response.put("pagination", pagination);
        response.put("status", "success");
        
        return response;
    }

    /**
     * Save a query template.
     *
     * @param templateRequest The template request
     * @return The template ID
     */
    public Map<String, Object> saveQueryTemplate(Object templateRequest) {
        // In a real implementation, this would save the template to the repository
        // For this sample, we'll return a mock response
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("templateId", UUID.randomUUID().toString());
        
        return response;
    }

    /**
     * Get query templates.
     *
     * @return The query templates
     */
    public Map<String, Object> getQueryTemplates() {
        Map<String, Object> response = new HashMap<>();
        
        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // Get templates from repository
        List<QueryTemplate> templates = templateRepository.findByUsername(username);
        
        List<Map<String, Object>> templateList = new ArrayList<>();
        
        for (QueryTemplate template : templates) {
            Map<String, Object> templateItem = new HashMap<>();
            templateItem.put("id", template.getId());
            templateItem.put("name", template.getName());
            templateItem.put("description", template.getDescription());
            templateItem.put("query", template.getQuery());
            templateItem.put("queryType", template.getQueryType().toString());
            
            templateList.add(templateItem);
        }
        
        response.put("templates", templateList);
        response.put("status", "success");
        
        return response;
    }
}