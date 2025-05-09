# MongoDB Chat Service - Angular Frontend

This is the Angular frontend for the MongoDB Chat Service application. It provides a user-friendly interface for interacting with the database chat service, allowing users to query MongoDB using natural language.

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

## Getting Started

### Prerequisites

- Node.js (v16.x or later)
- npm (v8.x or later)
- Angular CLI (v16.x or later)

### Installation

1. Install dependencies:

```bash
cd frontend
npm install
```

2. Start the development server:

```bash
npm start
```

The application will be available at `http://localhost:4200`.

## Building for Production

```bash
npm run build
```

The build artifacts will be stored in the `dist/` directory.

## Features

- Natural language querying of MongoDB databases
- Chat-based interface for database interactions
- Syntax highlighting for query results
- History of past conversations
- Dark/light theme support
- Responsive design for mobile and desktop

## API Integration

The frontend communicates with the backend API using the following services:

- `ApiService`: Core service for HTTP requests
- `ChatService`: Service for chat-related API calls
- `AuthService`: Service for authentication

## State Management

The application uses NgRx for state management:

- Actions: Define the actions that can be dispatched
- Reducers: Handle state changes based on actions
- Selectors: Extract specific pieces of state
- Effects: Handle side effects like API calls

## Styling

The application uses:

- SCSS for styling
- Angular Material components
- CSS variables for theming
- Flexbox and Grid for layout

## Testing

Run the tests with:

```bash
npm test
```

## Further Help

For more information on Angular development, check out the [Angular Documentation](https://angular.io/docs).
