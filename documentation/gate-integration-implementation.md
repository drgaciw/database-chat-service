# GATE Integration Implementation Guide

This document provides step-by-step instructions for implementing the GATE (General Architecture for Text Engineering) integration with the MongoDB Chat Query Service as described in the `gate-integration.md` document.

## 1. Add GATE Dependencies

First, add the GATE dependencies to your `pom.xml` file:

```xml
<!-- GATE Dependencies -->
<dependency>
    <groupId>uk.ac.gate</groupId>
    <artifactId>gate-core</artifactId>
    <version>9.0.1</version>
</dependency>
<dependency>
    <groupId>uk.ac.gate</groupId>
    <artifactId>gate-compiler-jdt</artifactId>
    <version>9.0.1</version>
</dependency>
```

## 2. Create GATE Resources

Create the following directory structure for GATE resources:

```
src/main/resources/gate/mongodb/
├── jape/
│   ├── main.jape
│   ├── intent.jape
│   ├── entities.jape
│   └── query.jape
├── lists.def
├── collections.lst
└── fields.lst
```

### 2.1 JAPE Grammar Files

#### main.jape
```jape
/*
 * Main JAPE grammar for MongoDB query processing
 */

MultiPhase: MongoDBQueryProcessing

Phases:
// First identify the query intent
intent.jape
// Then identify collections and fields
entities.jape
// Finally build the MongoDB query
query.jape
```

#### intent.jape
```jape
/*
 * JAPE grammar for identifying query intent
 */

Phase: Intent
Input: Token SpaceToken
Options: control = appelt

Rule: FindIntent
(
  ({Token.string =~ "(?i)find|get|show|display|list|what"})
):find
-->
:find.Intent = {type = "find"}

Rule: CountIntent
(
  ({Token.string =~ "(?i)count|how many|number of"})
):count
-->
:count.Intent = {type = "count"}

Rule: AggregateIntent
(
  ({Token.string =~ "(?i)average|avg|sum|total|min|max"})
):aggregate
-->
:aggregate.Intent = {type = "aggregate"}
```

#### entities.jape
```jape
/*
 * JAPE grammar for identifying collections and fields
 */

Phase: Entities
Input: Token Lookup Intent
Options: control = appelt

Rule: Collection
(
  ({Lookup.majorType == "collection"})
):collection
-->
:collection.Collection = {name = :collection.Lookup.minorType}

Rule: Field
(
  ({Lookup.majorType == "field"})
):field
-->
:field.Field = {name = :field.Lookup.minorType}

Rule: Limit
(
  ({Token.string =~ "(?i)top|limit"} {Token.kind == "number"})
):limit
-->
:limit.Limit = {value = :limit.Token.string}
```

#### query.jape
```jape
/*
 * JAPE grammar for building MongoDB queries
 */

Phase: Query
Input: Intent Collection Field Limit
Options: control = appelt

Rule: FindQuery
(
  ({Intent.type == "find"})
  ({Collection})?
  ({Field})*
  ({Limit})?
):query
-->
{
  // Get the collection name
  String collection = "documents";
  AnnotationSet collectionSet = bindings.get("query").getContainedAnnotations().get("Collection");
  if (collectionSet != null && !collectionSet.isEmpty()) {
    Annotation collectionAnn = collectionSet.iterator().next();
    collection = (String) collectionAnn.getFeatures().get("name");
  }
  
  // Get the fields
  StringBuilder projection = new StringBuilder("{ ");
  AnnotationSet fieldSet = bindings.get("query").getContainedAnnotations().get("Field");
  if (fieldSet != null && !fieldSet.isEmpty()) {
    boolean first = true;
    for (Annotation fieldAnn : fieldSet) {
      if (!first) projection.append(", ");
      projection.append("\"").append(fieldAnn.getFeatures().get("name")).append("\": 1");
      first = false;
    }
  }
  projection.append(" }");
  
  // Get the limit
  String limit = "";
  AnnotationSet limitSet = bindings.get("query").getContainedAnnotations().get("Limit");
  if (limitSet != null && !limitSet.isEmpty()) {
    Annotation limitAnn = limitSet.iterator().next();
    limit = ".limit(" + limitAnn.getFeatures().get("value") + ")";
  }
  
  // Build the query
  String query = "db." + collection + ".find({}, " + projection + ")" + limit;
  
  // Create the MongoQuery annotation
  FeatureMap features = Factory.newFeatureMap();
  features.put("operation", "find");
  features.put("collection", collection);
  features.put("projection", projection.toString());
  if (!limit.isEmpty()) features.put("limit", limit);
  features.put("query", query);
  
  outputAS.add(bindings.get("query").firstNode(), bindings.get("query").lastNode(), "MongoQuery", features);
}
```

### 2.2 Gazetteer Lists

#### lists.def
```
collections.lst:collection:name
fields.lst:field:name
```

#### collections.lst
```
sales
products
customers
users
orders
```

#### fields.lst
```
total
product
customer
date
quarter
year
```

## 3. Create GateEnhancedNlpProcessor Class

Create a new class called `GateEnhancedNlpProcessor` in the `com.aciworldwide.database_chat_service.nlp` package:

```java
package com.aciworldwide.database_chat_service.nlp;

import com.aciworldwide.database_chat_service.model.UserRole;
import gate.*;
import gate.creole.*;
import gate.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Enhanced NLP processor using GATE (General Architecture for Text Engineering)
 * for more sophisticated natural language processing of database queries.
 */
@Component
public class GateEnhancedNlpProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(GateEnhancedNlpProcessor.class);
    
    private CorpusController annieController;
    private CorpusController mongoQueryController;
    private boolean initialized = false;
    
    /**
     * Initialize GATE and load required resources.
     */
    @PostConstruct
    public void initialize() {
        try {
            logger.info("Initializing GATE...");
            
            // Initialize GATE
            if (!Gate.isInitialised()) {
                Gate.init();
            }
            
            // Register ANNIE plugin
            File pluginsDir = Gate.getPluginsHome();
            if (pluginsDir == null) {
                // If GATE_HOME is not set, use a default location
                String userDir = System.getProperty("user.dir");
                pluginsDir = new File(userDir, "gate-plugins");
                if (!pluginsDir.exists()) {
                    pluginsDir.mkdirs();
                }
                Gate.setPluginsHome(pluginsDir);
            }
            
            File anniePlugin = new File(pluginsDir, "ANNIE");
            if (!anniePlugin.exists()) {
                // Download ANNIE plugin if not available
                logger.info("ANNIE plugin not found. Downloading...");
                downloadAnniePlugin(pluginsDir);
            }
            
            // Register ANNIE plugin
            Gate.getCreoleRegister().registerDirectories(anniePlugin.toURI().toURL());
            
            // Load ANNIE with default parameters
            loadAnnie();
            
            // Load custom MongoDB query processing pipeline
            loadMongoQueryPipeline();
            
            initialized = true;
            logger.info("GATE initialization completed successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize GATE", e);
            throw new RuntimeException("Failed to initialize GATE", e);
        }
    }
    
    /**
     * Clean up GATE resources.
     */
    @PreDestroy
    public void cleanup() {
        try {
            logger.info("Cleaning up GATE resources...");
            Factory.deleteResource(annieController);
            Factory.deleteResource(mongoQueryController);
            Gate.shutdown();
        } catch (Exception e) {
            logger.error("Error during GATE cleanup", e);
        }
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
        if (!initialized) {
            logger.warn("GATE not initialized. Falling back to basic NLP processing.");
            return query;
        }
        
        try {
            logger.info("Processing query with GATE: {}", query);
            
            // Create a GATE document from the query
            Document document = Factory.newDocument(query);
            
            // Create a corpus with this document
            Corpus corpus = Factory.newCorpus("Temp Corpus");
            corpus.add(document);
            
            // Add collection hint as document feature if provided
            if (collectionHint != null && !collectionHint.isEmpty()) {
                document.getFeatures().put("collectionHint", collectionHint);
            }
            
            // Add user role as document feature
            document.getFeatures().put("userRole", userRole.toString());
            
            // Run ANNIE first for basic NLP processing
            annieController.setCorpus(corpus);
            annieController.execute();
            
            // Then run MongoDB query processing
            mongoQueryController.setCorpus(corpus);
            mongoQueryController.execute();
            
            // Extract MongoDB query from document annotations
            String mongoQuery = extractMongoQuery(document);
            
            // Clean up resources
            corpus.clear();
            Factory.deleteResource(corpus);
            Factory.deleteResource(document);
            
            logger.info("Generated MongoDB query: {}", mongoQuery);
            return mongoQuery;
        } catch (Exception e) {
            logger.error("Error processing query with GATE", e);
            throw new RuntimeException("Error processing query with GATE", e);
        }
    }
    
    /**
     * Extract MongoDB query from document annotations.
     *
     * @param document The processed GATE document
     * @return The MongoDB query
     */
    private String extractMongoQuery(Document document) {
        // Get the default annotation set
        AnnotationSet defaultAnnots = document.getAnnotations();
        
        // Look for MongoQuery annotations
        AnnotationSet mongoQueryAnnots = defaultAnnots.get("MongoQuery");
        
        if (mongoQueryAnnots != null && !mongoQueryAnnots.isEmpty()) {
            // Get the first MongoQuery annotation
            Annotation mongoQueryAnnot = mongoQueryAnnots.iterator().next();
            
            // Get the features
            FeatureMap features = mongoQueryAnnot.getFeatures();
            
            // Check if we have a generated query
            if (features.containsKey("query")) {
                return (String) features.get("query");
            }
            
            // Otherwise, build the query from the features
            return buildMongoQueryFromFeatures(features, document);
        }
        
        // If no MongoQuery annotation found, return the original text
        return document.getContent().toString();
    }
    
    /**
     * Build a MongoDB query from annotation features.
     *
     * @param features The features from the MongoQuery annotation
     * @param document The GATE document
     * @return The MongoDB query
     */
    private String buildMongoQueryFromFeatures(FeatureMap features, Document document) {
        StringBuilder queryBuilder = new StringBuilder();
        
        // Get operation type (find, count, aggregate)
        String operation = (String) features.getOrDefault("operation", "find");
        
        // Get collection name
        String collection = (String) features.getOrDefault("collection", "documents");
        
        // Start building the query
        queryBuilder.append("db.").append(collection).append(".");
        
        switch (operation) {
            case "find":
                queryBuilder.append("find(");
                break;
            case "count":
                queryBuilder.append("countDocuments(");
                break;
            case "aggregate":
                queryBuilder.append("aggregate([");
                break;
            default:
                queryBuilder.append("find(");
                break;
        }
        
        // Add conditions if present
        if (features.containsKey("conditions")) {
            queryBuilder.append(features.get("conditions"));
        } else {
            queryBuilder.append("{}");
        }
        
        // Add projection if present and operation is find
        if ("find".equals(operation) && features.containsKey("projection")) {
            queryBuilder.append(", ").append(features.get("projection"));
        }
        
        // Close the main operation
        if ("aggregate".equals(operation)) {
            queryBuilder.append("]");
        }
        queryBuilder.append(")");
        
        // Add sort if present and operation is find
        if ("find".equals(operation) && features.containsKey("sort")) {
            queryBuilder.append(".sort(").append(features.get("sort")).append(")");
        }
        
        // Add limit if present and operation is find
        if ("find".equals(operation) && features.containsKey("limit")) {
            queryBuilder.append(".limit(").append(features.get("limit")).append(")");
        }
        
        return queryBuilder.toString();
    }
    
    /**
     * Load ANNIE (A Nearly-New Information Extraction System).
     */
    private void loadAnnie() throws ResourceInstantiationException, MalformedURLException {
        logger.info("Loading ANNIE...");
        
        // Create a serial analyzer controller
        annieController = (CorpusController) Factory.createResource(
                "gate.creole.SerialAnalyserController",
                Factory.newFeatureMap(),
                Factory.newFeatureMap(),
                "ANNIE_" + Gate.genSym()
        );
        
        // Create the processing resources
        FeatureMap params = Factory.newFeatureMap();
        
        // Document Reset PR
        ProcessingResource docResetPR = (ProcessingResource) Factory.createResource(
                "gate.creole.annotdelete.AnnotationDeletePR",
                params
        );
        annieController.add(docResetPR);
        
        // English Tokenizer
        params = Factory.newFeatureMap();
        ProcessingResource tokenizerPR = (ProcessingResource) Factory.createResource(
                "gate.creole.tokeniser.DefaultTokeniser",
                params
        );
        annieController.add(tokenizerPR);
        
        // Gazetteer
        params = Factory.newFeatureMap();
        ProcessingResource gazetteerPR = (ProcessingResource) Factory.createResource(
                "gate.creole.gazetteer.DefaultGazetteer",
                params
        );
        annieController.add(gazetteerPR);
        
        // Sentence Splitter
        params = Factory.newFeatureMap();
        ProcessingResource sentenceSplitterPR = (ProcessingResource) Factory.createResource(
                "gate.creole.splitter.SentenceSplitter",
                params
        );
        annieController.add(sentenceSplitterPR);
        
        // POS Tagger
        params = Factory.newFeatureMap();
        ProcessingResource posTaggerPR = (ProcessingResource) Factory.createResource(
                "gate.creole.POSTagger",
                params
        );
        annieController.add(posTaggerPR);
        
        logger.info("ANNIE loaded successfully");
    }
    
    /**
     * Load MongoDB query processing pipeline.
     */
    private void loadMongoQueryPipeline() throws ResourceInstantiationException, IOException {
        logger.info("Loading MongoDB query processing pipeline...");
        
        // Create a serial analyzer controller
        mongoQueryController = (CorpusController) Factory.createResource(
                "gate.creole.SerialAnalyserController",
                Factory.newFeatureMap(),
                Factory.newFeatureMap(),
                "MongoQueryProcessor_" + Gate.genSym()
        );
        
        // Create custom gazetteers for MongoDB
        createMongoDbGazetteers();
        
        // Load MongoDB collection gazetteer
        FeatureMap params = Factory.newFeatureMap();
        params.put("listsURL", getClass().getResource("/gate/mongodb/lists.def"));
        ProcessingResource mongoGazetteerPR = (ProcessingResource) Factory.createResource(
                "gate.creole.gazetteer.DefaultGazetteer",
                params
        );
        mongoQueryController.add(mongoGazetteerPR);
        
        // Load MongoDB query JAPE grammar
        params = Factory.newFeatureMap();
        params.put("grammarURL", getClass().getResource("/gate/mongodb/jape/main.jape"));
        ProcessingResource mongoJapePR = (ProcessingResource) Factory.createResource(
                "gate.creole.Transducer",
                params
        );
        mongoQueryController.add(mongoJapePR);
        
        logger.info("MongoDB query processing pipeline loaded successfully");
    }
    
    /**
     * Create custom gazetteers for MongoDB.
     */
    private void createMongoDbGazetteers() {
        // This would create the necessary gazetteer files
        // For now, we'll assume they're already in the resources directory
    }
    
    /**
     * Download ANNIE plugin if not available.
     */
    private void downloadAnniePlugin(File pluginsDir) {
        // In a real implementation, this would download the ANNIE plugin
        // For now, we'll just log a message
        logger.info("ANNIE plugin download not implemented. Please install ANNIE manually.");
    }
}
```

## 4. Update NlpProcessor to Use GateEnhancedNlpProcessor

Modify the existing `NlpProcessor` class to use the `GateEnhancedNlpProcessor` when available:

```java
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
    private final GateEnhancedNlpProcessor gateProcessor;

    public NlpProcessor(MongoTemplate mongoTemplate, GateEnhancedNlpProcessor gateProcessor) {
        this.mongoTemplate = mongoTemplate;
        this.gateProcessor = gateProcessor;
        
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
        
        try {
            // Try to use GATE for processing
            return gateProcessor.processQuery(query, userRole, collectionHint);
        } catch (Exception e) {
            logger.warn("GATE processing failed, falling back to basic NLP: {}", e.getMessage());
            
            // Fall back to basic NLP processing
            return processQueryBasic(query, userRole, collectionHint);
        }
    }
    
    /**
     * Process a natural language query using basic NLP techniques.
     *
     * @param query The natural language query
     * @param userRole The user role (business or admin)
     * @param collectionHint Optional collection hint
     * @return The MongoDB query
     */
    private String processQueryBasic(String query, UserRole userRole, String collectionHint) {
        // Sanitize the input query
        String sanitizedQuery = sanitizeQuery(query);
        
        // Normalize the query (lowercase, remove punctuation, etc.)
        String normalizedQuery = normalizeQuery(sanitizedQuery);
        
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
    
    // ... rest of the existing NlpProcessor methods ...
}
```

## 5. Testing the GATE Integration

Create a test class to verify the GATE integration:

```java
package com.aciworldwide.database_chat_service.nlp;

import com.aciworldwide.database_chat_service.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class GateEnhancedNlpProcessorTest {

    @Autowired
    private GateEnhancedNlpProcessor gateProcessor;
    
    @Test
    public void testProcessQuery() {
        // Test a simple query
        String query = "Find all sales";
        String result = gateProcessor.processQuery(query, UserRole.BUSINESS, null);
        
        assertNotNull(result);
        assertTrue(result.contains("db.sales.find"));
        
        // Test a more complex query
        query = "Show me the top 5 products by sales in Q1 2025";
        result = gateProcessor.processQuery(query, UserRole.BUSINESS, null);
        
        assertNotNull(result);
        assertTrue(result.contains("db.products.find"));
        assertTrue(result.contains("limit(5)"));
    }
}
```

## 6. Troubleshooting

If you encounter issues with the GATE integration, check the following:

1. **GATE Initialization**: Make sure GATE is properly initialized. Check the logs for any initialization errors.

2. **ANNIE Plugin**: Ensure the ANNIE plugin is available. If not, you may need to download it manually.

3. **JAPE Grammar**: Verify that the JAPE grammar files are correctly formatted and located in the right directory.

4. **Gazetteer Lists**: Check that the gazetteer lists are properly formatted and contain the necessary entries.

5. **Memory Issues**: GATE can be memory-intensive. You may need to increase the JVM heap size using the `-Xmx` option.

## 7. Next Steps

After implementing the basic GATE integration, consider the following enhancements:

1. **Expand Gazetteer Lists**: Add more collection and field names to the gazetteer lists.

2. **Improve JAPE Grammars**: Enhance the JAPE grammars to handle more complex queries.

3. **Add Support for Conditions**: Extend the JAPE grammars to extract conditions from queries.

4. **Implement Caching**: Cache processed queries to improve performance.

5. **Add Support for Aggregation**: Enhance the JAPE grammars to handle aggregation queries.