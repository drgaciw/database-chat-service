package com.aciworldwide.database_chat_service.controller;

import com.aciworldwide.database_chat_service.model.QueryRequest;
import com.aciworldwide.database_chat_service.model.QueryResponse;
import com.aciworldwide.database_chat_service.service.QueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling database queries.
 * Provides endpoints for submitting natural language and structured queries.
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Query API", description = "API for querying MongoDB using natural language or structured queries")
@SecurityRequirement(name = "bearerAuth")
public class QueryController {

    private final QueryService queryService;

    public QueryController(QueryService queryService) {
        this.queryService = queryService;
    }

    /**
     * Submit a query to the database.
     * 
     * @param queryRequest The query request containing the query and related parameters
     * @return The query results
     */
    @PostMapping("/query")
    @Operation(
        summary = "Submit a query",
        description = "Submit a natural language or structured query to the MongoDB database",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Query executed successfully",
                content = @Content(schema = @Schema(implementation = QueryResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid query or parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "422", description = "Query could not be processed"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public ResponseEntity<QueryResponse> submitQuery(
            @Valid @RequestBody QueryRequest queryRequest) {
        
        QueryResponse response = queryService.processQuery(queryRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Get metadata about the database.
     * 
     * @param collection Optional collection name to get metadata for
     * @return The database metadata
     */
    @GetMapping("/metadata")
    @Operation(
        summary = "Get database metadata",
        description = "Retrieve metadata about the MongoDB database (collections, schemas, etc.)",
        responses = {
            @ApiResponse(responseCode = "200", description = "Metadata retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getDatabaseMetadata(
            @Parameter(description = "Collection name (optional)")
            @RequestParam(required = false) String collection) {
        
        return ResponseEntity.ok(queryService.getDatabaseMetadata(collection));
    }

    /**
     * Get query history for the current user.
     * 
     * @param limit Maximum number of history items to return
     * @param offset Offset for pagination
     * @return The query history
     */
    @GetMapping("/history")
    @Operation(
        summary = "Get query history",
        description = "Retrieve the user's query history",
        responses = {
            @ApiResponse(responseCode = "200", description = "History retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public ResponseEntity<?> getQueryHistory(
            @Parameter(description = "Maximum number of history items")
            @RequestParam(defaultValue = "10") int limit,
            
            @Parameter(description = "Offset for pagination")
            @RequestParam(defaultValue = "0") int offset) {
        
        return ResponseEntity.ok(queryService.getQueryHistory(limit, offset));
    }

    /**
     * Save a query template.
     * 
     * @param templateRequest The template request
     * @return The created template ID
     */
    @PostMapping("/templates")
    @Operation(
        summary = "Save query template",
        description = "Save a query as a reusable template",
        responses = {
            @ApiResponse(responseCode = "200", description = "Template saved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid template"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public ResponseEntity<?> saveQueryTemplate(@Valid @RequestBody Object templateRequest) {
        return ResponseEntity.ok(queryService.saveQueryTemplate(templateRequest));
    }

    /**
     * Get query templates.
     * 
     * @return The query templates
     */
    @GetMapping("/templates")
    @Operation(
        summary = "Get query templates",
        description = "Retrieve saved query templates",
        responses = {
            @ApiResponse(responseCode = "200", description = "Templates retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public ResponseEntity<?> getQueryTemplates() {
        return ResponseEntity.ok(queryService.getQueryTemplates());
    }
}