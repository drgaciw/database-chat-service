package com.aciworldwide.database_chat_service.service;

import com.aciworldwide.database_chat_service.model.QueryRequest;
import com.aciworldwide.database_chat_service.model.QueryResponse;
import com.aciworldwide.database_chat_service.model.QueryType;
import com.aciworldwide.database_chat_service.model.UserRole;
import com.aciworldwide.database_chat_service.nlp.NlpProcessor;
import com.aciworldwide.database_chat_service.repository.HistoryRepository;
import com.aciworldwide.database_chat_service.repository.TemplateRepository;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
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
    public QueryResponse processQuery(QueryRequest request) {
        logger.info("Processing query: {}", request.getQuery());
        
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
                executedQuery = request.getQuery();
            }
            
            // Execute the query
            results = executeQuery(executedQuery, request.getCollection());
            
            // Save query to history
            saveQueryToHistory(request, executedQuery, true);
            
            // Format the results based on user role and requested format
            return formatResults(results, request, System.currentTimeMillis() - startTime);
            
        } catch (Exception e) {
            logger.error("Error processing query", e);
            
            // Save failed query to history
            saveQueryToHistory(request, e.getMessage(), false);
            
            // Return error response
            return QueryResponse.builder()
                    .status("error")
                    .error(e.getMessage())
                    .build();
        }
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
     * Execute a MongoDB query and return the results.
     *
     * @param query The MongoDB query
     * @param collection The collection to query (optional)
     * @return The query results
     */
    private List<Document> executeQuery(String query, String collection) {
        // In a real implementation, this would parse and execute the MongoDB query
        // For this sample, we'll return a mock result
        
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
     */
    private void saveQueryToHistory(QueryRequest request, String executedQuery, boolean success) {
        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // Create history entry
        Document historyEntry = new Document();
        historyEntry.append("username", username);
        historyEntry.append("query", request.getQuery());
        historyEntry.append("queryType", request.getQueryType().toString());
        historyEntry.append("executedQuery", executedQuery);
        historyEntry.append("timestamp", Date.from(Instant.now()));
        historyEntry.append("status", success ? "success" : "error");
        
        // In a real implementation, this would save to the history repository
        logger.info("Saving query to history: {}", historyEntry);
    }

    /**
     * Get database metadata.
     *
     * @param collection Optional collection name
     * @return The database metadata
     */
    public Map<String, Object> getDatabaseMetadata(String collection) {
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
        
        return metadata;
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
        
        // In a real implementation, this would get history from the repository
        // For this sample, we'll return mock data
        
        List<Map<String, Object>> history = new ArrayList<>();
        
        for (int i = 0; i < limit; i++) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("id", UUID.randomUUID().toString());
            entry.put("query", "Sample query " + (i + offset));
            entry.put("queryType", i % 2 == 0 ? "NATURAL" : "STRUCTURED");
            entry.put("executedQuery", "db.collection.find({})");
            entry.put("timestamp", Date.from(Instant.now().minusSeconds(i * 3600)));
            entry.put("status", "success");
            
            history.add(entry);
        }
        
        response.put("history", history);
        
        Map<String, Object> pagination = new HashMap<>();
        pagination.put("total", 100);
        pagination.put("limit", limit);
        pagination.put("offset", offset);
        
        response.put("pagination", pagination);
        
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
        
        // In a real implementation, this would get templates from the repository
        // For this sample, we'll return mock data
        
        List<Map<String, Object>> templates = new ArrayList<>();
        
        for (int i = 0; i < 5; i++) {
            Map<String, Object> template = new HashMap<>();
            template.put("id", UUID.randomUUID().toString());
            template.put("name", "Template " + (i + 1));
            template.put("description", "Description for template " + (i + 1));
            template.put("query", "Sample query " + (i + 1));
            template.put("queryType", i % 2 == 0 ? "NATURAL" : "STRUCTURED");
            
            templates.add(template);
        }
        
        response.put("status", "success");
        response.put("templates", templates);
        
        return response;
    }
}