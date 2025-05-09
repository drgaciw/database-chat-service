package com.aciworldwide.database_chat_service.nlp;

import com.aciworldwide.database_chat_service.model.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Processor for natural language queries.
 * Translates natural language into MongoDB queries.
 */
@Component
public class NlpProcessor {

    private static final Logger logger = LoggerFactory.getLogger(NlpProcessor.class);

    private final MongoTemplate mongoTemplate;
    private final Map<String, String> collectionAliases;
    private final Map<String, String> fieldAliases;
    private final Map<String, Pattern> intentPatterns;

    public NlpProcessor(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        
        // Initialize collection aliases (business terms to collection names)
        this.collectionAliases = initializeCollectionAliases();
        
        // Initialize field aliases (business terms to field names)
        this.fieldAliases = initializeFieldAliases();
        
        // Initialize intent patterns
        this.intentPatterns = initializeIntentPatterns();
    }

    /**
     * Process a natural language query and convert it to a MongoDB query.
     *
     * @param query The natural language query
     * @param userRole The user role (business or admin)
     * @param collectionHint Optional collection hint
     * @return The MongoDB query
     */
    public String processQuery(String query, UserRole userRole, String collectionHint) {
        logger.info("Processing natural language query: {}", query);
        
        // Normalize the query (lowercase, remove punctuation, etc.)
        String normalizedQuery = normalizeQuery(query);
        
        // Identify the intent
        String intent = identifyIntent(normalizedQuery);
        logger.info("Identified intent: {}", intent);
        
        // Extract entities
        Map<String, Object> entities = extractEntities(normalizedQuery, collectionHint);
        logger.info("Extracted entities: {}", entities);
        
        // Build the MongoDB query
        String mongoQuery = buildMongoQuery(intent, entities, userRole);
        logger.info("Built MongoDB query: {}", mongoQuery);
        
        return mongoQuery;
    }

    /**
     * Normalize a query by converting to lowercase, removing punctuation, etc.
     *
     * @param query The query to normalize
     * @return The normalized query
     */
    private String normalizeQuery(String query) {
        // Convert to lowercase
        String normalized = query.toLowerCase();
        
        // Remove question marks
        normalized = normalized.replace("?", "");
        
        // Replace multiple spaces with a single space
        normalized = normalized.replaceAll("\\s+", " ").trim();
        
        return normalized;
    }

    /**
     * Identify the intent of a query.
     *
     * @param query The normalized query
     * @return The identified intent
     */
    private String identifyIntent(String query) {
        // Check for each intent pattern
        for (Map.Entry<String, Pattern> entry : intentPatterns.entrySet()) {
            Matcher matcher = entry.getValue().matcher(query);
            if (matcher.find()) {
                return entry.getKey();
            }
        }
        
        // Default to find intent
        return "find";
    }

    /**
     * Extract entities from a query.
     *
     * @param query The normalized query
     * @param collectionHint Optional collection hint
     * @return The extracted entities
     */
    private Map<String, Object> extractEntities(String query, String collectionHint) {
        Map<String, Object> entities = new HashMap<>();
        
        // Extract collection
        String collection = extractCollection(query, collectionHint);
        entities.put("collection", collection);
        
        // Extract limit
        Integer limit = extractLimit(query);
        if (limit != null) {
            entities.put("limit", limit);
        }
        
        // Extract sort field and direction
        Map<String, Object> sort = extractSort(query);
        if (sort != null) {
            entities.put("sort", sort);
        }
        
        // Extract time period
        String timePeriod = extractTimePeriod(query);
        if (timePeriod != null) {
            entities.put("timePeriod", timePeriod);
        }
        
        // Extract fields to project
        List<String> fields = extractFields(query);
        if (!fields.isEmpty()) {
            entities.put("fields", fields);
        }
        
        // Extract conditions
        Map<String, Object> conditions = extractConditions(query);
        if (!conditions.isEmpty()) {
            entities.put("conditions", conditions);
        }
        
        return entities;
    }

    /**
     * Extract the collection from a query.
     *
     * @param query The normalized query
     * @param collectionHint Optional collection hint
     * @return The extracted collection
     */
    private String extractCollection(String query, String collectionHint) {
        // If a collection hint is provided, use it
        if (collectionHint != null && !collectionHint.isEmpty()) {
            return collectionHint;
        }
        
        // Check for collection aliases in the query
        for (Map.Entry<String, String> entry : collectionAliases.entrySet()) {
            if (query.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        // Default to a generic collection
        return "documents";
    }

    /**
     * Extract the limit from a query.
     *
     * @param query The normalized query
     * @return The extracted limit, or null if not found
     */
    private Integer extractLimit(String query) {
        // Check for "top N" pattern
        Pattern topPattern = Pattern.compile("top (\\d+)");
        Matcher topMatcher = topPattern.matcher(query);
        if (topMatcher.find()) {
            return Integer.parseInt(topMatcher.group(1));
        }
        
        // Check for "limit N" pattern
        Pattern limitPattern = Pattern.compile("limit (\\d+)");
        Matcher limitMatcher = limitPattern.matcher(query);
        if (limitMatcher.find()) {
            return Integer.parseInt(limitMatcher.group(1));
        }
        
        return null;
    }

    /**
     * Extract the sort field and direction from a query.
     *
     * @param query The normalized query
     * @return The extracted sort field and direction, or null if not found
     */
    private Map<String, Object> extractSort(String query) {
        // Check for "by X" pattern (e.g., "by sales")
        Pattern byPattern = Pattern.compile("by (\\w+)");
        Matcher byMatcher = byPattern.matcher(query);
        if (byMatcher.find()) {
            String field = byMatcher.group(1);
            
            // Map to actual field name if it's an alias
            if (fieldAliases.containsKey(field)) {
                field = fieldAliases.get(field);
            }
            
            // Determine sort direction
            int direction = query.contains("ascending") ? 1 : -1;
            
            Map<String, Object> sort = new HashMap<>();
            sort.put("field", field);
            sort.put("direction", direction);
            
            return sort;
        }
        
        return null;
    }

    /**
     * Extract the time period from a query.
     *
     * @param query The normalized query
     * @return The extracted time period, or null if not found
     */
    private String extractTimePeriod(String query) {
        // Check for quarter pattern (e.g., "Q1 2025")
        Pattern quarterPattern = Pattern.compile("q(\\d) (\\d{4})");
        Matcher quarterMatcher = quarterPattern.matcher(query);
        if (quarterMatcher.find()) {
            String quarter = quarterMatcher.group(1);
            String year = quarterMatcher.group(2);
            return "Q" + quarter + "-" + year;
        }
        
        // Check for year pattern (e.g., "2025")
        Pattern yearPattern = Pattern.compile("\\b(\\d{4})\\b");
        Matcher yearMatcher = yearPattern.matcher(query);
        if (yearMatcher.find()) {
            return yearMatcher.group(1);
        }
        
        return null;
    }

    /**
     * Extract fields to project from a query.
     *
     * @param query The normalized query
     * @return The extracted fields
     */
    private List<String> extractFields(String query) {
        List<String> fields = new ArrayList<>();
        
        // Check for "show X, Y, Z" pattern
        Pattern showPattern = Pattern.compile("show (\\w+(, \\w+)*)");
        Matcher showMatcher = showPattern.matcher(query);
        if (showMatcher.find()) {
            String fieldList = showMatcher.group(1);
            String[] fieldArray = fieldList.split(", ");
            
            for (String field : fieldArray) {
                // Map to actual field name if it's an alias
                if (fieldAliases.containsKey(field)) {
                    field = fieldAliases.get(field);
                }
                
                fields.add(field);
            }
        }
        
        return fields;
    }

    /**
     * Extract conditions from a query.
     *
     * @param query The normalized query
     * @return The extracted conditions
     */
    private Map<String, Object> extractConditions(String query) {
        Map<String, Object> conditions = new HashMap<>();
        
        // Add time period condition if present
        String timePeriod = extractTimePeriod(query);
        if (timePeriod != null) {
            if (timePeriod.startsWith("Q")) {
                conditions.put("quarter", timePeriod);
            } else {
                conditions.put("year", timePeriod);
            }
        }
        
        // Add other conditions based on the query
        // This would be more sophisticated in a real implementation
        
        return conditions;
    }

    /**
     * Build a MongoDB query based on the intent and entities.
     *
     * @param intent The query intent
     * @param entities The extracted entities
     * @param userRole The user role
     * @return The MongoDB query
     */
    private String buildMongoQuery(String intent, Map<String, Object> entities, UserRole userRole) {
        StringBuilder queryBuilder = new StringBuilder();
        
        String collection = (String) entities.get("collection");
        
        // Start building the query based on the intent
        switch (intent) {
            case "find":
                queryBuilder.append("db.").append(collection).append(".find(");
                
                // Add conditions
                if (entities.containsKey("conditions")) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> conditions = (Map<String, Object>) entities.get("conditions");
                    queryBuilder.append(buildConditionsJson(conditions));
                } else {
                    queryBuilder.append("{}");
                }
                
                // Add projection
                if (entities.containsKey("fields")) {
                    @SuppressWarnings("unchecked")
                    List<String> fields = (List<String>) entities.get("fields");
                    queryBuilder.append(", ").append(buildProjectionJson(fields));
                }
                
                queryBuilder.append(")");
                
                // Add sort
                if (entities.containsKey("sort")) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> sort = (Map<String, Object>) entities.get("sort");
                    queryBuilder.append(".sort({ \"")
                            .append(sort.get("field"))
                            .append("\": ")
                            .append(sort.get("direction"))
                            .append(" })");
                }
                
                // Add limit
                if (entities.containsKey("limit")) {
                    queryBuilder.append(".limit(").append(entities.get("limit")).append(")");
                }
                
                break;
                
            case "count":
                queryBuilder.append("db.").append(collection).append(".countDocuments(");
                
                // Add conditions
                if (entities.containsKey("conditions")) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> conditions = (Map<String, Object>) entities.get("conditions");
                    queryBuilder.append(buildConditionsJson(conditions));
                } else {
                    queryBuilder.append("{}");
                }
                
                queryBuilder.append(")");
                break;
                
            case "aggregate":
                queryBuilder.append("db.").append(collection).append(".aggregate([");
                
                // Add match stage if conditions exist
                if (entities.containsKey("conditions")) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> conditions = (Map<String, Object>) entities.get("conditions");
                    queryBuilder.append("{ $match: ").append(buildConditionsJson(conditions)).append(" }");
                }
                
                // Add group stage (simplified for this example)
                queryBuilder.append("])");
                break;
                
            default:
                queryBuilder.append("db.").append(collection).append(".find({})");
                break;
        }
        
        return queryBuilder.toString();
    }

    /**
     * Build a JSON string for query conditions.
     *
     * @param conditions The conditions map
     * @return The JSON string
     */
    private String buildConditionsJson(Map<String, Object> conditions) {
        if (conditions.isEmpty()) {
            return "{}";
        }
        
        StringBuilder jsonBuilder = new StringBuilder("{ ");
        
        boolean first = true;
        for (Map.Entry<String, Object> entry : conditions.entrySet()) {
            if (!first) {
                jsonBuilder.append(", ");
            }
            
            jsonBuilder.append("\"").append(entry.getKey()).append("\": ");
            
            if (entry.getValue() instanceof String) {
                jsonBuilder.append("\"").append(entry.getValue()).append("\"");
            } else {
                jsonBuilder.append(entry.getValue());
            }
            
            first = false;
        }
        
        jsonBuilder.append(" }");
        
        return jsonBuilder.toString();
    }

    /**
     * Build a JSON string for query projection.
     *
     * @param fields The fields to project
     * @return The JSON string
     */
    private String buildProjectionJson(List<String> fields) {
        if (fields.isEmpty()) {
            return "{}";
        }
        
        StringBuilder jsonBuilder = new StringBuilder("{ ");
        
        boolean first = true;
        for (String field : fields) {
            if (!first) {
                jsonBuilder.append(", ");
            }
            
            jsonBuilder.append("\"").append(field).append("\": 1");
            
            first = false;
        }
        
        jsonBuilder.append(" }");
        
        return jsonBuilder.toString();
    }

    /**
     * Initialize collection aliases.
     *
     * @return The collection aliases map
     */
    private Map<String, String> initializeCollectionAliases() {
        Map<String, String> aliases = new HashMap<>();
        
        // Business terms to collection names
        aliases.put("sales", "sales");
        aliases.put("products", "products");
        aliases.put("customers", "customers");
        aliases.put("users", "users");
        aliases.put("orders", "orders");
        
        return aliases;
    }

    /**
     * Initialize field aliases.
     *
     * @return The field aliases map
     */
    private Map<String, String> initializeFieldAliases() {
        Map<String, String> aliases = new HashMap<>();
        
        // Business terms to field names
        aliases.put("sales", "total");
        aliases.put("revenue", "total");
        aliases.put("product", "product");
        aliases.put("customer", "customer");
        aliases.put("date", "date");
        aliases.put("quarter", "quarter");
        aliases.put("year", "year");
        
        return aliases;
    }

    /**
     * Initialize intent patterns.
     *
     * @return The intent patterns map
     */
    private Map<String, Pattern> initializeIntentPatterns() {
        Map<String, Pattern> patterns = new HashMap<>();
        
        // Find intent patterns
        patterns.put("find", Pattern.compile("(show|get|find|what|list)"));
        
        // Count intent patterns
        patterns.put("count", Pattern.compile("(how many|count|number of)"));
        
        // Aggregate intent patterns
        patterns.put("aggregate", Pattern.compile("(average|sum|total|min|max)"));
        
        return patterns;
    }
}