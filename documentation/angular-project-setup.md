# Angular Project Setup Guide

## Overview

This document provides instructions for setting up the Angular frontend for the MongoDB Chat Service. The frontend provides a user-friendly interface for interacting with the database chat service, allowing users to query MongoDB using natural language.

## Prerequisites

- Node.js (v16.x or later)
- npm (v8.x or later)
- Angular CLI (v16.x or later)

## Installation

### 1. Install Angular CLI

```bash
npm install -g @angular/cli
```

### 2. Create a New Angular Project

```bash
ng new database-chat-frontend --style=scss --routing=true
cd database-chat-frontend
```

### 3. Install Required Dependencies

```bash
# Core dependencies
npm install @angular/material @angular/cdk @angular/flex-layout
npm install @ngrx/store @ngrx/effects @ngrx/entity @ngrx/router-store @ngrx/store-devtools
npm install ngx-markdown marked prismjs ngx-skeleton-loader

# Development dependencies
npm install --save-dev @angular-eslint/builder @angular-eslint/eslint-plugin
```

## Project Structure

The project follows a modular architecture with the following structure:

```
src/
├── app/
│   ├── core/                 # Core module (services, guards, interceptors)
│   │   ├── services/
│   │   ├── guards/
│   │   ├── interceptors/
│   │   └── core.module.ts
│   ├── shared/               # Shared module (components, directives, pipes)
│   │   ├── components/
│   │   ├── directives/
│   │   ├── pipes/
│   │   └── shared.module.ts
│   ├── features/             # Feature modules
│   │   ├── chat/             # Chat feature
│   │   │   ├── components/
│   │   │   ├── services/
│   │   │   ├── store/
│   │   │   └── chat.module.ts
│   │   └── admin/            # Admin feature (if needed)
│   ├── app-routing.module.ts
│   ├── app.component.ts
│   └── app.module.ts
├── assets/                   # Static assets
├── environments/             # Environment configurations
└── styles/                   # Global styles
    ├── abstracts/            # Variables, mixins, functions
    ├── base/                 # Base styles, typography, reset
    ├── components/           # Component styles
    ├── layout/               # Layout styles
    ├── pages/                # Page-specific styles
    └── themes/               # Theme configurations
```

## Configuration

### 1. Configure Angular Material

```bash
ng add @angular/material
```

Select a theme (e.g., "Indigo/Pink"), set up global Angular Material typography, and include browser animations.

### 2. Configure Environment Variables

Update the environment files to include API URLs:

**src/environments/environment.ts**:
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

**src/environments/environment.prod.ts**:
```typescript
export const environment = {
  production: true,
  apiUrl: '/api'  // Relative URL for production
};
```

### 3. Configure Path Aliases

Update `tsconfig.json` to include path aliases for easier imports:

```json
{
  "compilerOptions": {
    // ... other options
    "paths": {
      "@app/*": ["src/app/*"],
      "@core/*": ["src/app/core/*"],
      "@shared/*": ["src/app/shared/*"],
      "@features/*": ["src/app/features/*"],
      "@env/*": ["src/environments/*"]
    }
  }
}
```

## Setting Up Core Module

Create a core module to handle application-wide services:

```bash
ng generate module core
```

Add the following services:

```bash
ng generate service core/services/api
ng generate service core/services/auth
ng generate service core/services/error-handler
```

## Setting Up Shared Module

Create a shared module for reusable components:

```bash
ng generate module shared
```

## Setting Up Feature Modules

Create the chat feature module:

```bash
ng generate module features/chat --routing
```

## Running the Application

```bash
ng serve
```

The application will be available at `http://localhost:4200`.

## Building for Production

```bash
ng build --configuration=production
```

The build artifacts will be stored in the `dist/` directory.

## Next Steps

After setting up the project structure, proceed to implement:

1. Core services for API communication
2. Shared components for UI elements
3. Feature modules for specific functionality
4. State management with NgRx
5. Styling with SCSS

Refer to the [Component Structure Documentation](./angular-component-structure.md) for details on implementing components.
