@echo off
echo Setting up Angular project for MongoDB Chat Service...

echo Installing Angular CLI globally...
call npm install -g @angular/cli

echo Creating new Angular project...
call ng new database-chat-frontend --style=scss --routing=true --skip-git --directory=.

echo Installing additional dependencies...
call npm install @angular/material @angular/cdk @angular/flex-layout
call npm install @ngrx/store @ngrx/effects @ngrx/entity @ngrx/router-store @ngrx/store-devtools
call npm install ngx-markdown marked prismjs ngx-skeleton-loader
call npm install --save-dev @angular-eslint/builder @angular-eslint/eslint-plugin

echo Setup complete! You can now run 'npm start' to start the development server.
echo.
