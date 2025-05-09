# API Specification Document

## Overview

This document specifies the REST API endpoints for the MongoDB Chat Query Microservice. The API enables users to submit natural language or structured queries to a MongoDB database and receive formatted results, as well as manage chat messages and conversations.

## Base URL

```
https://api.example.com/v1
```

## Authentication

All API requests require authentication using JWT (JSON Web Tokens).

**Headers:**
```
Authorization: Bearer {jwt_token}
```

## WebSocket Connection

For real-time updates, the service provides WebSocket endpoints:

```
ws://api.example.com/v1/chat/ws
```

### WebSocket Events

1. **Message Events:**
   - `message:new` - New message received
   - `message:update` - Message status updated
   - `message:delete` - Message deleted

2. **Typing Events:**
   - `typing:start` - User started typing
   - `typing:stop` - User stopped typing

3. **Status Events:**
   - `status:online` - User came online
   - `status:offline` - User went offline

## Endpoints

### 1. Submit Query

**Endpoint:** `POST /query`

**Description:** Submit a natural language or structured query to the MongoDB database.

**Request Body:**
```json
{
  "query": "string",         // Required: Natural language or structured query
  "queryType": "string",     // Required: "natural" or "structured"
  "userRole": "string",      // Required: "business" or "admin"
  "format": "string",        // Optional: "json", "table", "chart" (default: "json")
  "limit": "number",         // Optional: Maximum number of results (default: 100)
  "collection": "string"     // Optional: Specific collection to query (if known)
}
```

**Response:**
```json
{
  "status": "string",        // "success" or "error"
  "executedQuery": "string", // The MongoDB query that was executed
  "results": [],             // Array of results or formatted data
  "metadata": {              // Optional metadata about the results
    "count": "number",       // Number of results returned
    "totalCount": "number",  // Total number of matching documents
    "executionTime": "number" // Query execution time in milliseconds
  },
  "visualization": {},       // Optional visualization data if format is "chart"
  "error": "string"          // Error message if status is "error"
}
```

**Example Request (Natural Language):**
```json
{
  "query": "What are the top 5 products by sales in Q1 2025?",
  "queryType": "natural",
  "userRole": "business",
  "format": "table"
}
```

**Example Request (Structured):**
```json
{
  "query": "db.sales.find({ \"quarter\": \"Q1-2025\" }).sort({ \"total\": -1 }).limit(5)",
  "queryType": "structured",
  "userRole": "admin",
  "format": "json"
}
```

### 2. Chat Messages

#### Send Message

**Endpoint:** `POST /chat/messages`

**Description:** Send a new chat message.

**Request Body:**
```json
{
  "content": "string",       // Required: Message content
  "parentId": "string",      // Optional: ID of parent message for threading
  "conversationId": "string" // Optional: ID of conversation
}
```

**Response:**
```json
{
  "id": "string",
  "content": "string",
  "role": "string",          // "user" or "assistant"
  "timestamp": "string",     // ISO timestamp
  "status": "string",        // "SENT", "DELIVERED", "READ"
  "parentId": "string",      // If part of a thread
  "conversationId": "string"
}
```

#### Search Messages

**Endpoint:** `GET /chat/messages/search`

**Description:** Search for messages containing specific text.

**Query Parameters:**
```
query: string    // Required: Search query
page: number     // Optional: Page number (default: 1)
limit: number    // Optional: Results per page (default: 20)
```

**Response:**
```json
{
  "status": "string",
  "results": [
    {
      "id": "string",
      "content": "string",
      "role": "string",
      "timestamp": "string",
      "score": "number",     // Search relevance score
      "context": {           // Context around the match
        "before": ["string"],
        "after": ["string"]
      }
    }
  ],
  "pagination": {
    "total": "number",
    "page": "number",
    "limit": "number"
  }
}
```

#### Get Message History

**Endpoint:** `GET /chat/messages/{messageId}/history`

**Description:** Get the edit history of a message.

**Query Parameters:**
```
limit: number    // Optional: Number of history items (default: 5)
```

**Response:**
```json
{
  "status": "string",
  "history": [
    {
      "id": "string",
      "content": "string",
      "timestamp": "string",
      "editedBy": "string"
    }
  ]
}
```

#### Get Message Replies

**Endpoint:** `GET /chat/messages/{messageId}/replies`

**Description:** Get replies to a message.

**Query Parameters:**
```
page: number     // Optional: Page number (default: 1)
limit: number    // Optional: Results per page (default: 50)
```

**Response:**
```json
{
  "status": "string",
  "replies": [
    {
      "id": "string",
      "content": "string",
      "role": "string",
      "timestamp": "string",
      "status": "string"
    }
  ],
  "pagination": {
    "total": "number",
    "page": "number",
    "limit": "number"
  }
}
```

#### Update Message Status

**Endpoint:** `POST /chat/messages/{messageId}/status`

**Description:** Update the status of a message.

**Request Body:**
```json
{
  "status": "string"  // Required: "SENT", "DELIVERED", "READ"
}
```

**Response:**
```json
{
  "status": "string",
  "message": {
    "id": "string",
    "status": "string",
    "updatedAt": "string"
  }
}
```

### 3. Get Database Metadata

**Endpoint:** `GET /metadata`

**Description:** Retrieve metadata about the MongoDB database (collections, schemas, etc.).

**Query Parameters:**
```
collection: string  // Optional: Specific collection to get metadata for
```

**Response:**
```json
{
  "status": "string",        // "success" or "error"
  "database": "string",      // Database name
  "collections": [           // Array of collections
    {
      "name": "string",      // Collection name
      "count": "number",     // Document count
      "size": "number",      // Collection size in bytes
      "indexes": [],         // Array of indexes
      "sampleSchema": {}     // Sample document schema
    }
  ],
  "error": "string"          // Error message if status is "error"
}
```

### 4. Get Query History

**Endpoint:** `GET /history`

**Description:** Retrieve the user's query history.

**Query Parameters:**
```
limit: number       // Optional: Maximum number of history items (default: 10)
offset: number      // Optional: Offset for pagination (default: 0)
```

**Response:**
```json
{
  "status": "string",        // "success" or "error"
  "history": [               // Array of query history items
    {
      "id": "string",        // Query ID
      "query": "string",     // Original query
      "queryType": "string", // "natural" or "structured"
      "executedQuery": "string", // MongoDB query that was executed
      "timestamp": "string", // ISO timestamp
      "status": "string"     // "success" or "error"
    }
  ],
  "pagination": {
    "total": "number",       // Total number of history items
    "limit": "number",       // Limit used
    "offset": "number"       // Offset used
  },
  "error": "string"          // Error message if status is "error"
}
```

### 5. Query Templates

#### Save Query Template

**Endpoint:** `POST /templates`

**Description:** Save a query as a reusable template.

**Request Body:**
```json
{
  "name": "string",          // Required: Template name
  "description": "string",   // Optional: Template description
  "query": "string",         // Required: Natural language or structured query
  "queryType": "string",     // Required: "natural" or "structured"
  "parameters": []           // Optional: Array of parameters that can be replaced
}
```

**Response:**
```json
{
  "status": "string",        // "success" or "error"
  "templateId": "string",    // ID of the created template
  "error": "string"          // Error message if status is "error"
}
```

#### Get Query Templates

**Endpoint:** `GET /templates`

**Description:** Retrieve saved query templates.

**Response:**
```json
{
  "status": "string",        // "success" or "error"
  "templates": [             // Array of templates
    {
      "id": "string",        // Template ID
      "name": "string",      // Template name
      "description": "string", // Template description
      "query": "string",     // Original query
      "queryType": "string", // "natural" or "structured"
      "parameters": []       // Array of parameters that can be replaced
    }
  ],
  "error": "string"          // Error message if status is "error"
}
```

## Error Codes

| Status Code | Description |
|-------------|-------------|
| 200 | Success |
| 400 | Bad Request - Invalid query or parameters |
| 401 | Unauthorized - Authentication required |
| 403 | Forbidden - Insufficient permissions |
| 404 | Not Found - Resource not found |
| 422 | Unprocessable Entity - Query could not be processed |
| 500 | Internal Server Error |

## Rate Limiting

API requests are limited to 100 requests per minute per user. Rate limit information is included in the response headers:

```
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 99
X-RateLimit-Reset: 1616979027
```

## Versioning

The API is versioned through the URL path (e.g., `/v1/query`). When breaking changes are introduced, a new version will be released.