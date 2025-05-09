package com.aciworldwide.database_chat_service.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Represents a query template.
 * Templates are reusable queries that can be parameterized.
 */
@Data
@Document(collection = "query_templates")
public class QueryTemplate {
    
    @Id
    private String id;
    
    /**
     * Name of the template.
     */
    private String name;
    
    /**
     * Description of the template.
     */
    private String description;
    
    /**
     * The query string.
     */
    private String query;
    
    /**
     * The type of query (NATURAL or STRUCTURED).
     */
    private QueryType queryType;
    
    /**
     * Username of the user who created the template.
     */
    private String username;
    
    /**
     * List of parameters that can be replaced in the query.
     */
    private List<TemplateParameter> parameters;
    
    /**
     * Represents a parameter in a query template.
     */
    @Data
    public static class TemplateParameter {
        
        /**
         * Name of the parameter.
         */
        private String name;
        
        /**
         * Description of the parameter.
         */
        private String description;
        
        /**
         * Default value for the parameter.
         */
        private String defaultValue;
    }
}