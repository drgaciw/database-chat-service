# Angular Deployment Guide

This document provides instructions for deploying the Angular frontend for the MongoDB Chat Service in various environments.

## Building for Production

Before deploying, you need to build the Angular application for production:

```bash
cd frontend
npm run build
```

This will create a `dist/database-chat-frontend` directory with the compiled application.

## Deployment Options

### 1. Deploying with Spring Boot

The simplest approach is to deploy the Angular application with the Spring Boot backend:

1. Build the Angular application as described above
2. Copy the contents of `dist/database-chat-frontend` to `src/main/resources/static` in your Spring Boot project
3. Build and deploy the Spring Boot application

This approach is detailed in the [Angular and Spring Boot Integration Guide](./angular-spring-integration.md).

### 2. Deploying to a Static Web Server

You can deploy the Angular application to any static web server:

#### Apache

1. Copy the contents of `dist/database-chat-frontend` to your Apache document root (e.g., `/var/www/html`)
2. Create an `.htaccess` file in the document root with the following content:

```
<IfModule mod_rewrite.c>
  RewriteEngine On
  RewriteBase /
  RewriteRule ^index\.html$ - [L]
  RewriteCond %{REQUEST_FILENAME} !-f
  RewriteCond %{REQUEST_FILENAME} !-d
  RewriteRule . /index.html [L]
</IfModule>
```

3. Enable the `mod_rewrite` module if it's not already enabled:

```bash
sudo a2enmod rewrite
sudo systemctl restart apache2
```

#### Nginx

1. Copy the contents of `dist/database-chat-frontend` to your Nginx document root (e.g., `/usr/share/nginx/html`)
2. Configure Nginx to handle Angular's client-side routing:

```nginx
server {
    listen 80;
    server_name your-domain.com;
    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    # API proxy configuration
    location /api/ {
        proxy_pass http://backend-server:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

3. Restart Nginx:

```bash
sudo systemctl restart nginx
```

### 3. Deploying to a Docker Container

You can deploy the Angular application in a Docker container:

1. Create a `Dockerfile` in the frontend directory:

```dockerfile
# Stage 1: Build the Angular application
FROM node:16 as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Stage 2: Serve the application with Nginx
FROM nginx:alpine
COPY --from=build /app/dist/database-chat-frontend /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

2. Create an `nginx.conf` file:

```nginx
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    # API proxy configuration
    location /api/ {
        proxy_pass http://backend:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

3. Build and run the Docker container:

```bash
docker build -t mongodb-chat-frontend .
docker run -p 80:80 mongodb-chat-frontend
```

### 4. Deploying to Cloud Services

#### AWS S3 + CloudFront

1. Build the Angular application
2. Upload the contents of `dist/database-chat-frontend` to an S3 bucket
3. Configure the bucket for static website hosting
4. Create a CloudFront distribution pointing to the S3 bucket
5. Configure CloudFront to redirect 404s to `index.html`
6. Set up a custom domain (optional)

#### Azure Static Web Apps

1. Install the Azure Static Web Apps CLI:

```bash
npm install -g @azure/static-web-apps-cli
```

2. Build the Angular application
3. Deploy to Azure Static Web Apps:

```bash
swa deploy ./dist/database-chat-frontend --api-location https://your-backend-api.azurewebsites.net
```

#### Firebase Hosting

1. Install the Firebase CLI:

```bash
npm install -g firebase-tools
```

2. Initialize Firebase in your project:

```bash
firebase init
```

3. Select "Hosting" and follow the prompts
4. Build the Angular application
5. Deploy to Firebase:

```bash
firebase deploy
```

## Environment Configuration

For different deployment environments, you can use Angular's environment configuration:

1. Create environment-specific files in `src/environments/`:
   - `environment.ts` (development)
   - `environment.prod.ts` (production)
   - `environment.staging.ts` (staging)

2. Configure the build for different environments in `angular.json`:

```json
"configurations": {
  "production": {
    "fileReplacements": [
      {
        "replace": "src/environments/environment.ts",
        "with": "src/environments/environment.prod.ts"
      }
    ],
    ...
  },
  "staging": {
    "fileReplacements": [
      {
        "replace": "src/environments/environment.ts",
        "with": "src/environments/environment.staging.ts"
      }
    ],
    ...
  }
}
```

3. Build for a specific environment:

```bash
ng build --configuration=staging
```

## API Configuration

Ensure that the API URL is correctly configured for each environment:

```typescript
// environment.ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};

// environment.prod.ts
export const environment = {
  production: true,
  apiUrl: '/api'  // Relative URL when deployed with backend
  // Or absolute URL: 'https://api.your-domain.com/api'
};
```

## Security Considerations

1. **HTTPS**: Always use HTTPS in production
2. **Content Security Policy**: Implement a CSP to prevent XSS attacks
3. **Environment Variables**: Don't hardcode sensitive information in your Angular code
4. **API Keys**: Store API keys securely and use server-side proxies when possible

## Performance Optimization

1. **Enable Gzip/Brotli Compression**: Configure your web server to compress responses
2. **Cache Control**: Set appropriate cache headers for static assets
3. **CDN**: Use a CDN for global distribution
4. **Lazy Loading**: Ensure modules are lazy-loaded when appropriate
5. **PWA**: Consider implementing Progressive Web App features

## Monitoring and Analytics

1. **Error Tracking**: Implement error tracking with a service like Sentry
2. **Analytics**: Add Google Analytics or a similar service
3. **Performance Monitoring**: Use tools like Lighthouse to monitor performance

## Continuous Deployment

Set up a CI/CD pipeline for automated deployments:

### GitHub Actions Example

Create a `.github/workflows/deploy.yml` file:

```yaml
name: Deploy Angular App

on:
  push:
    branches: [ main ]

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      
      - name: Set up Node.js
        uses: actions/setup-node@v2
        with:
          node-version: '16'
          
      - name: Install dependencies
        run: |
          cd frontend
          npm ci
          
      - name: Build
        run: |
          cd frontend
          npm run build
          
      - name: Deploy to server
        uses: SamKirkland/FTP-Deploy-Action@4.3.0
        with:
          server: ${{ secrets.FTP_SERVER }}
          username: ${{ secrets.FTP_USERNAME }}
          password: ${{ secrets.FTP_PASSWORD }}
          local-dir: frontend/dist/database-chat-frontend/
          server-dir: /public_html/
```

## Troubleshooting

### 404 Errors for Routes

If you're getting 404 errors when navigating to routes directly:

1. Ensure your web server is configured to redirect all requests to `index.html`
2. Check that the `<base href="/">` tag is set correctly in `index.html`

### API Connection Issues

If the frontend can't connect to the API:

1. Check CORS configuration on the backend
2. Verify that the API URL is correct for the environment
3. Test API endpoints directly using a tool like Postman

### Build Errors

If you encounter build errors:

1. Check for dependency conflicts
2. Verify that you're using compatible versions of Node.js and npm
3. Clear the cache: `npm cache clean --force`

## Conclusion

By following this guide, you should be able to deploy the Angular frontend for the MongoDB Chat Service in various environments. Choose the deployment option that best fits your infrastructure and requirements.
