#!/bin/bash
echo "Setting up Angular project for MongoDB Chat Service..."

echo "Installing Angular CLI globally..."
npm install -g @angular/cli

echo "Creating new Angular project..."
ng new database-chat-frontend --style=scss --routing=true --skip-git --directory=.

echo "Installing additional dependencies..."
npm install @angular/material @angular/cdk @angular/flex-layout
npm install @ngrx/store @ngrx/effects @ngrx/entity @ngrx/router-store @ngrx/store-devtools
npm install ngx-markdown marked prismjs ngx-skeleton-loader
npm install --save-dev @angular-eslint/builder @angular-eslint/eslint-plugin

echo "Setup complete! You can now run 'npm start' to start the development server."
echo
