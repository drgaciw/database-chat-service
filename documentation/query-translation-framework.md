# Query Translation Framework

## Overview

The Query Translation Framework is a core component of the MongoDB Chat Query Microservice that translates natural language queries into executable MongoDB queries. This document outlines the architecture, components, and workflow of the framework.

## Architecture

The Query Translation Framework follows a pipeline architecture with distinct stages for processing natural language input and generating MongoDB queries:

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│             │    │             │    │             │    │             │    │             │
│  Language   │───▶│  Intent     │───▶│  Entity     │───▶│  Query      │───▶│  Query      │
│  Parser     │    │  Recognizer │    │  Extractor  │    │  Builder    │    │  Optimizer  │
│             │    │             │    │             │    │             │    │             │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
```

## Components

### 1. Language Parser

**Responsibility**: Processes the natural language input to extract key phrases and structure.

**Key Functions**:
- Tokenization: Breaking the input into words and phrases
- Part-of-speech tagging: Identifying nouns, verbs, adjectives, etc.
- Dependency parsing: Understanding relationships between words
- Stopword removal: Filtering out common words with little semantic value

**Implementation Approach**:
- Initial: Simple tokenization and keyword matching
- Future: Integration with NLP libraries for more sophisticated parsing

### 2. Intent Recognizer

**Responsibility**: Determines the user's intent (what type of query they want to perform).

**Supported Intents**:
- Find: Retrieve documents matching specific criteria
- Count: Count documents matching specific criteria
- Aggregate: Perform aggregation operations (sum, average, etc.)
- Sort: Order results by specific fields
- Limit: Restrict the number of results
- Project: Select specific fields to return
- Schema: Get information about collection structure

**Implementation Approach**:
- Initial: Keyword-based intent recognition
  - Example: "What are the top 5 products" → Sort + Limit intent
  - Example: "How many users" → Count intent
- Future: Machine learning-based intent classification

### 3. Entity Extractor

**Responsibility**: Identifies entities (collections, fields, values, operators) from the parsed input.

**Entity Types**:
- Collections: The MongoDB collections to query
- Fields: The fields within collections
- Values: The values to match against fields
- Operators: Comparison operators (equals, greater than, etc.)
- Aggregation Functions: Sum, average, count, etc.
- Time Periods: Date ranges, quarters, years, etc.

**Implementation Approach**:
- Initial: Dictionary-based entity extraction with predefined mappings
  - Example: "sales in Q1 2025" → {"time_period": "Q1 2025", "collection": "sales"}
- Future: Named entity recognition with machine learning

### 4. Query Builder

**Responsibility**: Constructs MongoDB queries based on the recognized intent and extracted entities.

**Query Types**:
- Find Queries: `db.collection.find()`
- Count Queries: `db.collection.countDocuments()`
- Aggregation Queries: `db.collection.aggregate()`

**Implementation Approach**:
- Template-based query generation
- Mapping of intents to query structures
- Mapping of entities to query parameters

**Example Mappings**:
- Find Intent → `db.{collection}.find({criteria})`
- Count Intent → `db.{collection}.countDocuments({criteria})`
- Aggregate Intent → `db.{collection}.aggregate([{$match: {criteria}}, {$group: {...}}])`

### 5. Query Optimizer

**Responsibility**: Optimizes the generated MongoDB query for performance.

**Optimization Techniques**:
- Query structure optimization
- Index utilization
- Projection optimization (selecting only needed fields)
- Aggregation pipeline optimization

**Implementation Approach**:
- Rule-based optimization
- Query analysis and restructuring

## Workflow

### Business User Query Example

**Natural Language Query**: "What are the top 5 products by sales in Q1 2025?"

**1. Language Parser**:
- Tokenizes: ["What", "are", "the", "top", "5", "products", "by", "sales", "in", "Q1", "2025"]
- Identifies key phrases: "top 5", "products", "sales", "Q1 2025"

**2. Intent Recognizer**:
- Recognizes intents: Find + Sort + Limit
- "top" indicates Sort intent
- "5" indicates Limit intent

**3. Entity Extractor**:
- Collections: "products" or "sales" (determined by schema knowledge)
- Fields: "sales" (as sort field)
- Values: "Q1 2025" (time period)
- Limit: 5

**4. Query Builder**:
- Constructs query:
```javascript
db.sales.find(
  { "quarter": "Q1-2025" },
  { "product": 1, "total": 1 }
).sort({ "total": -1 }).limit(5)
```

**5. Query Optimizer**:
- Ensures indexes exist on "quarter" and "total" fields
- Optimizes projection to include only necessary fields

### Database Administrator Query Example

**Natural Language Query**: "How many documents are in the users collection?"

**1. Language Parser**:
- Tokenizes: ["How", "many", "documents", "are", "in", "the", "users", "collection"]
- Identifies key phrases: "how many", "documents", "users collection"

**2. Intent Recognizer**:
- Recognizes intent: Count

**3. Entity Extractor**:
- Collections: "users"

**4. Query Builder**:
- Constructs query:
```javascript
db.users.countDocuments({})
```

**5. Query Optimizer**:
- Determines that no optimization is needed for this simple query

## User Role Considerations

The framework adapts its behavior based on the user role:

### Business User Adaptations:
- Focus on business entities and metrics
- Prioritize readability of results
- Apply business-specific terminology mappings
- Limit access to sensitive collections/fields

### Database Administrator Adaptations:
- Support technical MongoDB commands
- Provide detailed metadata in results
- Allow more complex query structures
- Provide access to system collections and metadata

## Implementation Phases

### Phase 1: Keyword-Based System
- Simple tokenization and keyword matching
- Predefined mappings between phrases and MongoDB operations
- Support for basic query patterns
- Dictionary-based entity extraction

### Phase 2: Enhanced Pattern Recognition
- More sophisticated pattern matching
- Support for complex queries with multiple conditions
- Improved entity extraction with context awareness
- Query optimization

### Phase 3: Machine Learning Integration (Future)
- Integration with NLP models for intent classification
- Named entity recognition for better entity extraction
- Learning from user feedback to improve translations
- Context-aware query generation

## Error Handling

The framework includes robust error handling:

1. **Ambiguity Resolution**:
   - When multiple interpretations are possible, use context or ask for clarification
   - Example: "Show me sales" could refer to the "sales" collection or the "sales" field

2. **Unknown Entity Handling**:
   - When entities are not recognized, provide feedback and suggestions
   - Example: "Unknown collection 'customer'. Did you mean 'customers'?"

3. **Invalid Query Detection**:
   - Validate generated queries before execution
   - Provide clear error messages for invalid queries

4. **Fallback Mechanisms**:
   - When translation fails, offer alternative approaches
   - Allow users to refine their queries based on feedback

## Performance Considerations

1. **Caching**:
   - Cache translation results for similar queries
   - Cache schema information for faster entity recognition

2. **Query Complexity Limits**:
   - Set limits on query complexity to prevent performance issues
   - Warn users when queries might be slow

3. **Asynchronous Processing**:
   - Process complex translations asynchronously
   - Provide immediate feedback for simple queries

## Extension Points

The framework is designed with several extension points:

1. **Custom Entity Recognizers**:
   - Add domain-specific entity recognizers
   - Example: Financial terms recognizer for financial applications

2. **Intent Plugins**:
   - Add new intent handlers for specialized query types
   - Example: Geospatial query intent for location-based queries

3. **Query Templates**:
   - Define reusable query templates for common patterns
   - Example: "Monthly sales report" template

4. **Result Formatters**:
   - Add custom formatters for specific query types
   - Example: Chart formatter for time-series data

## Testing and Evaluation

The framework includes comprehensive testing:

1. **Unit Tests**:
   - Test individual components (parser, recognizer, etc.)
   - Verify correct handling of edge cases

2. **Integration Tests**:
   - Test end-to-end query translation
   - Verify correct MongoDB query generation

3. **Benchmark Tests**:
   - Measure translation performance
   - Ensure response time requirements are met

4. **User Acceptance Tests**:
   - Test with real-world queries from both user types
   - Gather feedback for continuous improvement

## Conclusion

The Query Translation Framework provides a flexible and extensible system for translating natural language queries into MongoDB queries. Starting with a keyword-based approach and designed for future enhancement with machine learning, it supports both business users and database administrators with appropriate query capabilities.