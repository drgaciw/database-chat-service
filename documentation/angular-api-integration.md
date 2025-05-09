# Angular API Integration Guide

## Overview

This document outlines the approach for integrating the Angular frontend with the MongoDB Chat Service backend API. It covers service implementation, authentication, error handling, and state management using NgRx.

## State Management Architecture

The frontend uses NgRx for state management with the following structure:

1. **Store**: Centralized state container
2. **Actions**: Define state changes
3. **Reducers**: Handle state changes
4. **Effects**: Manage side effects and API calls
5. **Selectors**: Provide access to state
6. **HTTP Interceptors**: Handle authentication, error handling, and logging
7. **API Services**: Provide typed methods for API endpoints

## Chat State Management

### State Interface

```typescript
export interface ChatState {
  messages: Message[];
  searchResults: Message[];
  messageHistory: Message[];
  loading: boolean;
  searching: boolean;
  loadingHistory: boolean;
  error: string | null;
  selectedMessage: Message | null;
  selectedThread: Message | null;
  threadMessages: Message[];
  typingUsers: Set<string>;
  hasMore: boolean;
}
```

### Actions

The chat feature uses the following actions:

1. **Message Loading**:
   - `loadMessages`: Load messages with pagination
   - `loadMessagesSuccess`: Handle successful message loading
   - `loadMessagesFailure`: Handle message loading errors

2. **Message Search**:
   - `searchMessages`: Search messages
   - `searchMessagesSuccess`: Handle successful search
   - `searchMessagesFailure`: Handle search errors

3. **Message History**:
   - `loadMessageHistory`: Load message history
   - `loadMessageHistorySuccess`: Handle successful history loading
   - `loadMessageHistoryFailure`: Handle history loading errors

4. **Message Sending**:
   - `sendMessage`: Send a new message
   - `sendMessageSuccess`: Handle successful message sending
   - `sendMessageFailure`: Handle message sending errors

5. **Thread Management**:
   - `openThread`: Open a message thread
   - `closeThread`: Close a message thread
   - `loadThreadMessages`: Load thread messages
   - `loadThreadMessagesSuccess`: Handle successful thread loading
   - `loadThreadMessagesFailure`: Handle thread loading errors

6. **Typing Indicators**:
   - `startTyping`: Start typing indicator
   - `stopTyping`: Stop typing indicator
   - `updateTypingUsers`: Update typing users list

7. **Message Selection**:
   - `selectMessage`: Select a message
   - `clearSelectedMessage`: Clear selected message

8. **Error Handling**:
   - `clearError`: Clear error state

### Effects

The chat feature uses the following effects:

1. **Message Loading Effect**:
   - Handles message loading with pagination
   - Updates loading state
   - Handles errors

2. **Search Effect**:
   - Handles message search
   - Updates search results
   - Handles errors

3. **Message History Effect**:
   - Handles message history loading
   - Updates history state
   - Handles errors

4. **Send Message Effect**:
   - Handles message sending
   - Updates messages state
   - Handles errors

5. **Thread Management Effect**:
   - Handles thread operations
   - Updates thread state
   - Handles errors

6. **Typing Effects**:
   - Handles typing indicators
   - Updates typing users state

### Selectors

The chat feature provides the following selectors:

1. **Message Selectors**:
   - `selectMessages`: Get all messages
   - `selectSearchResults`: Get search results
   - `selectMessageHistory`: Get message history
   - `selectThreadMessages`: Get thread messages

2. **Loading Selectors**:
   - `selectLoading`: Get loading state
   - `selectSearching`: Get searching state
   - `selectLoadingHistory`: Get history loading state

3. **Error Selectors**:
   - `selectError`: Get error state

4. **Selection Selectors**:
   - `selectSelectedMessage`: Get selected message
   - `selectSelectedThread`: Get selected thread

5. **Typing Selectors**:
   - `selectTypingUsers`: Get typing users

6. **Pagination Selectors**:
   - `selectHasMoreMessages`: Check if more messages are available

## API Service Architecture

The frontend uses a layered approach for API communication:

1. **HTTP Interceptors**: Handle authentication, error handling, and logging
2. **API Services**: Provide typed methods for API endpoints
3. **NgRx Effects**: Manage side effects and API calls from the store

## Core API Service

The core API service provides a base for all API communication:

```typescript
// core/services/api.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@env/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  get<T>(path: string, params: HttpParams = new HttpParams()): Observable<T> {
    return this.http.get<T>(`${this.apiUrl}${path}`, { params });
  }

  post<T>(path: string, body: any = {}): Observable<T> {
    return this.http.post<T>(`${this.apiUrl}${path}`, body);
  }

  put<T>(path: string, body: any = {}): Observable<T> {
    return this.http.put<T>(`${this.apiUrl}${path}`, body);
  }

  delete<T>(path: string): Observable<T> {
    return this.http.delete<T>(`${this.apiUrl}${path}`);
  }
}
```

## Feature-Specific Services

### Chat Service

```typescript
// features/chat/services/chat.service.ts
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '@core/services/api.service';
import { Message } from '../models/message.model';
import { Conversation } from '../models/conversation.model';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  constructor(private apiService: ApiService) {}

  // Get all conversations
  getConversations(): Observable<Conversation[]> {
    return this.apiService.get<Conversation[]>('/conversations');
  }

  // Get a specific conversation
  getConversation(id: string): Observable<Conversation> {
    return this.apiService.get<Conversation>(`/conversations/${id}`);
  }

  // Create a new conversation
  createConversation(title: string): Observable<Conversation> {
    return this.apiService.post<Conversation>('/conversations', { title });
  }

  // Get messages for a conversation
  getMessages(conversationId: string): Observable<Message[]> {
    return this.apiService.get<Message[]>(`/conversations/${conversationId}/messages`);
  }

  // Send a message
  sendMessage(conversationId: string, content: string): Observable<Message> {
    return this.apiService.post<Message>(`/conversations/${conversationId}/messages`, { content });
  }
}
```

### Query Service

```typescript
// features/query/services/query.service.ts
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '@core/services/api.service';
import { QueryResult } from '../models/query-result.model';

@Injectable({
  providedIn: 'root'
})
export class QueryService {
  constructor(private apiService: ApiService) {}

  // Execute a natural language query
  executeNaturalLanguageQuery(query: string): Observable<QueryResult> {
    return this.apiService.post<QueryResult>('/query/natural', { query });
  }

  // Execute a structured query
  executeStructuredQuery(query: object): Observable<QueryResult> {
    return this.apiService.post<QueryResult>('/query/structured', { query });
  }

  // Get query history
  getQueryHistory(): Observable<QueryResult[]> {
    return this.apiService.get<QueryResult[]>('/query/history');
  }

  // Save a query as a template
  saveQueryTemplate(name: string, query: string, isStructured: boolean): Observable<any> {
    return this.apiService.post('/query/templates', {
      name,
      query,
      isStructured
    });
  }

  // Get query templates
  getQueryTemplates(): Observable<any[]> {
    return this.apiService.get<any[]>('/query/templates');
  }
}
```

## Authentication Integration

### Auth Service

```typescript
// core/services/auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { environment } from '@env/environment';
import { User } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();
  
  private apiUrl = environment.apiUrl;
  
  constructor(private http: HttpClient) {
    // Check for stored user on initialization
    const storedUser = localStorage.getItem('currentUser');
    if (storedUser) {
      this.currentUserSubject.next(JSON.parse(storedUser));
    }
  }
  
  login(username: string, password: string): Observable<User> {
    return this.http.post<{ token: string, user: User }>(`${this.apiUrl}/auth/login`, { username, password })
      .pipe(
        map(response => {
          // Store user details and token
          const user = response.user;
          user.token = response.token;
          localStorage.setItem('currentUser', JSON.stringify(user));
          this.currentUserSubject.next(user);
          return user;
        })
      );
  }
  
  logout(): void {
    // Remove user from local storage
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }
  
  isLoggedIn(): boolean {
    return !!this.currentUserSubject.value;
  }
  
  getToken(): string | null {
    return this.currentUserSubject.value?.token || null;
  }
}
```

### Auth Interceptor

```typescript
// core/interceptors/auth.interceptor.ts
import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // Get the auth token
    const token = this.authService.getToken();
    
    // Clone the request and add the authorization header if token exists
    if (token) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }
    
    return next.handle(request);
  }
}
```

## Error Handling

### Error Interceptor

```typescript
// core/interceptors/error.interceptor.ts
import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import { ErrorHandlerService } from '../services/error-handler.service';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(
    private authService: AuthService,
    private errorHandler: ErrorHandlerService
  ) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        let errorMessage = 'An unknown error occurred';
        
        // Handle different error statuses
        if (error.status === 401) {
          // Unauthorized - log out user
          this.authService.logout();
          errorMessage = 'Your session has expired. Please log in again.';
        } else if (error.status === 403) {
          // Forbidden
          errorMessage = 'You do not have permission to perform this action.';
        } else if (error.status === 404) {
          // Not found
          errorMessage = 'The requested resource was not found.';
        } else if (error.status === 500) {
          // Server error
          errorMessage = 'A server error occurred. Please try again later.';
        }
        
        // Use custom error message if available
        if (error.error && error.error.message) {
          errorMessage = error.error.message;
        }
        
        // Log and notify about the error
        this.errorHandler.handleError(errorMessage);
        
        return throwError(() => new Error(errorMessage));
      })
    );
  }
}
```

### Error Handler Service

```typescript
// core/services/error-handler.service.ts
import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ErrorHandlerService {
  private errorSubject = new BehaviorSubject<string | null>(null);
  public error$ = this.errorSubject.asObservable();
  
  constructor(private snackBar: MatSnackBar) {}
  
  handleError(message: string): void {
    // Update error state
    this.errorSubject.next(message);
    
    // Show error notification
    this.snackBar.open(message, 'Dismiss', {
      duration: 5000,
      panelClass: ['error-snackbar']
    });
    
    // Log error to console in development
    console.error(message);
  }
  
  clearError(): void {
    this.errorSubject.next(null);
  }
}
```

## Server-Sent Events (SSE) Integration

For real-time updates and streaming responses:

```typescript
// core/services/sse.service.ts
import { Injectable, NgZone } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '@env/environment';

export interface EventSourceMessage {
  data: string;
  type: string;
  id: string;
  retry?: number;
}

@Injectable({
  providedIn: 'root'
})
export class SseService {
  private apiUrl = environment.apiUrl;

  constructor(private zone: NgZone) {}

  getServerSentEvents(path: string, options?: any): Observable<EventSourceMessage> {
    return new Observable<EventSourceMessage>(observer => {
      const url = new URL(`${this.apiUrl}${path}`);
      
      // Add query parameters if provided
      if (options) {
        Object.keys(options).forEach(key => {
          url.searchParams.append(key, options[key]);
        });
      }
      
      const eventSource = new EventSource(url.toString());
      
      eventSource.onmessage = event => {
        this.zone.run(() => {
          observer.next({
            data: event.data,
            type: event.type,
            id: event.lastEventId
          });
        });
      };
      
      eventSource.onerror = error => {
        this.zone.run(() => {
          observer.error(error);
        });
      };
      
      return () => {
        eventSource.close();
      };
    });
  }
}
```

## API Models

Define TypeScript interfaces for API models:

```typescript
// features/chat/models/message.model.ts
export interface Message {
  id: string;
  conversationId: string;
  content: string;
  role: 'user' | 'assistant';
  timestamp: string;
}

// features/chat/models/conversation.model.ts
export interface Conversation {
  id: string;
  title: string;
  createdAt: string;
  updatedAt: string;
  messageCount: number;
}

// features/query/models/query-result.model.ts
export interface QueryResult {
  id: string;
  query: string;
  result: any;
  executionTime: number;
  timestamp: string;
  status: 'success' | 'error';
  errorMessage?: string;
}
```

## API Configuration

Configure the API in the environment files:

```typescript
// environments/environment.ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api/v1'
};

// environments/environment.prod.ts
export const environment = {
  production: true,
  apiUrl: '/api/v1'
};
```

## API Documentation

Generate API documentation using Swagger/OpenAPI:

```typescript
// app.module.ts
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AuthInterceptor } from './core/interceptors/auth.interceptor';
import { ErrorInterceptor } from './core/interceptors/error.interceptor';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
```

## Testing API Integration

### Service Tests

```typescript
// features/chat/services/chat.service.spec.ts
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ChatService } from './chat.service';
import { environment } from '@env/environment';

describe('ChatService', () => {
  let service: ChatService;
  let httpMock: HttpTestingController;
  const apiUrl = environment.apiUrl;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ChatService]
    });
    service = TestBed.inject(ChatService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get conversations', () => {
    const mockConversations = [
      { id: '1', title: 'Conversation 1', createdAt: '2023-01-01', updatedAt: '2023-01-01', messageCount: 5 },
      { id: '2', title: 'Conversation 2', createdAt: '2023-01-02', updatedAt: '2023-01-02', messageCount: 3 }
    ];

    service.getConversations().subscribe(conversations => {
      expect(conversations).toEqual(mockConversations);
    });

    const req = httpMock.expectOne(`${apiUrl}/conversations`);
    expect(req.request.method).toBe('GET');
    req.flush(mockConversations);
  });

  it('should send a message', () => {
    const mockMessage = {
      id: '1',
      conversationId: '1',
      content: 'Hello',
      role: 'user',
      timestamp: '2023-01-01T12:00:00Z'
    };

    service.sendMessage('1', 'Hello').subscribe(message => {
      expect(message).toEqual(mockMessage);
    });

    const req = httpMock.expectOne(`${apiUrl}/conversations/1/messages`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ content: 'Hello' });
    req.flush(mockMessage);
  });
});
```

## Next Steps

After implementing the API integration:

1. Implement authentication and authorization
2. Add error handling and logging
3. Implement real-time updates with SSE
4. Add caching for improved performance
5. Implement offline support
6. Add unit tests for NgRx store
7. Add integration tests for real-time features
8. Implement error recovery strategies
9. Add retry mechanisms for failed requests

Refer to the [UI/UX Implementation Guide](./angular-ui-ux-implementation.md) for details on implementing the user interface.
