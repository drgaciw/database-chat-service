# Angular and Spring Boot Integration Guide

This document provides instructions for integrating the Angular frontend with the Spring Boot backend for the MongoDB Chat Service.

## Overview

The integration between Angular and Spring Boot involves:

1. Configuring CORS in the Spring Boot application
2. Setting up proxy configuration in Angular
3. Building and deploying the Angular app with the Spring Boot application

## Configuring CORS in Spring Boot

Add the following configuration to your Spring Boot application to enable CORS:

```java
package com.aciworldwide.database_chat_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Allow all origins in development
        config.addAllowedOrigin("http://localhost:4200");
        
        // Add production origins as needed
        // config.addAllowedOrigin("https://your-production-domain.com");
        
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);
        
        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }
}
```

## Setting Up Proxy Configuration in Angular

Create a `proxy.conf.json` file in the Angular project root:

```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true
  }
}
```

Update the `package.json` file to use the proxy configuration:

```json
"scripts": {
  "start": "ng serve --proxy-config proxy.conf.json",
  ...
}
```

## Building and Deploying Together

### Option 1: Manual Integration

1. Build the Angular application:

```bash
cd frontend
npm run build
```

2. Copy the contents of `dist/database-chat-frontend` to `src/main/resources/static` in your Spring Boot project.

3. Build and run the Spring Boot application:

```bash
./mvnw clean package
java -jar target/database-chat-service-0.0.1-SNAPSHOT.jar
```

### Option 2: Maven Integration

1. Add the `frontend-maven-plugin` to your `pom.xml`:

```xml
<plugin>
    <groupId>com.github.eirslett</groupId>
    <artifactId>frontend-maven-plugin</artifactId>
    <version>1.12.1</version>
    <configuration>
        <workingDirectory>frontend</workingDirectory>
        <installDirectory>target</installDirectory>
    </configuration>
    <executions>
        <execution>
            <id>install node and npm</id>
            <goals>
                <goal>install-node-and-npm</goal>
            </goals>
            <configuration>
                <nodeVersion>v16.14.0</nodeVersion>
                <npmVersion>8.5.0</npmVersion>
            </configuration>
        </execution>
        <execution>
            <id>npm install</id>
            <goals>
                <goal>npm</goal>
            </goals>
            <configuration>
                <arguments>install</arguments>
            </configuration>
        </execution>
        <execution>
            <id>npm run build</id>
            <goals>
                <goal>npm</goal>
            </goals>
            <configuration>
                <arguments>run build</arguments>
            </configuration>
        </execution>
    </executions>
</plugin>
```

2. Add a resource configuration to copy the Angular build to the Spring Boot static resources:

```xml
<plugin>
    <artifactId>maven-resources-plugin</artifactId>
    <executions>
        <execution>
            <id>copy-resources</id>
            <phase>validate</phase>
            <goals>
                <goal>copy-resources</goal>
            </goals>
            <configuration>
                <outputDirectory>${project.build.directory}/classes/static</outputDirectory>
                <resources>
                    <resource>
                        <directory>frontend/dist/database-chat-frontend</directory>
                    </resource>
                </resources>
            </configuration>
        </execution>
    </executions>
</plugin>
```

3. Build the entire application with Maven:

```bash
./mvnw clean package
```

## Handling Routes in Production

To ensure that Angular's client-side routing works correctly when deployed with Spring Boot, add a controller to forward all non-API requests to the Angular app:

```java
package com.aciworldwide.database_chat_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaController {

    @RequestMapping(value = "/{path:[^\\.]*}")
    public String redirect() {
        return "forward:/";
    }
}
```

## Environment Configuration

Create environment-specific configuration files in Angular:

**environment.ts** (development):
```typescript
export const environment = {
  production: false,
  apiUrl: '/api'
};
```

**environment.prod.ts** (production):
```typescript
export const environment = {
  production: true,
  apiUrl: '/api'
};
```

## Security Considerations

1. **CSRF Protection**: If using Spring Security with CSRF protection, configure Angular to include the CSRF token in requests:

```typescript
// core/interceptors/csrf.interceptor.ts
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CookieService } from 'ngx-cookie-service';

@Injectable()
export class CsrfInterceptor implements HttpInterceptor {
  constructor(private cookieService: CookieService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const csrfToken = this.cookieService.get('XSRF-TOKEN');
    
    if (csrfToken && (req.method === 'POST' || req.method === 'PUT' || 
                      req.method === 'DELETE' || req.method === 'PATCH')) {
      req = req.clone({
        headers: req.headers.set('X-XSRF-TOKEN', csrfToken)
      });
    }
    
    return next.handle(req);
  }
}
```

2. **Authentication**: Use JWT or session-based authentication as appropriate for your application.

## Testing the Integration

1. Start the Spring Boot application:

```bash
./mvnw spring-boot:run
```

2. Start the Angular development server with proxy configuration:

```bash
cd frontend
npm start
```

3. Access the application at `http://localhost:4200`

## Troubleshooting

### CORS Issues

If you encounter CORS issues:

1. Check that the CORS configuration in Spring Boot is correct
2. Verify that the proxy configuration in Angular is set up properly
3. Ensure that the correct origins are allowed in the CORS configuration

### 404 Errors for Angular Routes

If Angular routes return 404 errors when deployed with Spring Boot:

1. Verify that the SpaController is correctly configured
2. Check that the Angular app is being served from the correct location

### API Endpoint Issues

If API calls are failing:

1. Check that the API URLs in Angular are correct
2. Verify that the proxy configuration is working
3. Test the API endpoints directly using a tool like Postman

## Conclusion

By following this guide, you should have a fully integrated Angular frontend and Spring Boot backend for the MongoDB Chat Service. The application will provide a seamless user experience with client-side routing and API communication.
