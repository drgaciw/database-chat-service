# Angular Component Structure

## Overview

This document outlines the component structure for the MongoDB Chat Service Angular frontend. The application follows a modular architecture with feature-based organization, focusing on reusability and maintainability.

## Core Components

### App Component

The root component that serves as the application shell.

```typescript
// app.component.ts
import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  template: `
    <app-header></app-header>
    <main class="main-content">
      <router-outlet></router-outlet>
    </main>
    <app-footer></app-footer>
  `,
  styleUrls: ['./app.component.scss']
})
export class AppComponent { }
```

### Layout Components

#### Header Component

```typescript
// header.component.ts
import { Component } from '@angular/core';

@Component({
  selector: 'app-header',
  template: `
    <header class="app-header">
      <div class="logo">
        <img src="assets/images/logo.svg" alt="MongoDB Chat">
        <h1>MongoDB Chat</h1>
      </div>
      <nav class="main-nav">
        <a routerLink="/chat" routerLinkActive="active">Chat</a>
        <a routerLink="/history" routerLinkActive="active">History</a>
        <a routerLink="/settings" routerLinkActive="active">Settings</a>
      </nav>
      <div class="user-controls">
        <app-theme-toggle></app-theme-toggle>
        <app-user-menu></app-user-menu>
      </div>
    </header>
  `,
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent { }
```

#### Footer Component

```typescript
// footer.component.ts
import { Component } from '@angular/core';

@Component({
  selector: 'app-footer',
  template: `
    <footer class="app-footer">
      <div class="footer-content">
        <p>&copy; 2025 MongoDB Chat Service</p>
        <div class="footer-links">
          <a href="/about">About</a>
          <a href="/privacy">Privacy</a>
          <a href="/terms">Terms</a>
        </div>
      </div>
    </footer>
  `,
  styleUrls: ['./footer.component.scss']
})
export class FooterComponent { }
```

## Feature Components

### Chat Module

The Chat module contains components for the chat interface, including message display, input, and conversation management.

#### Chat Component

The main container component for the chat feature.

```typescript
// chat.component.ts
import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { Message } from '../models/message.model';
import * as ChatActions from '../store/chat.actions';
import * as ChatSelectors from '../store/chat.selectors';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.scss']
})
export class ChatComponent implements OnInit {
  messages$: Observable<Message[]>;
  loading$: Observable<boolean>;
  error$: Observable<string | null>;

  constructor(private store: Store) {
    this.messages$ = this.store.select(ChatSelectors.selectAllMessages);
    this.loading$ = this.store.select(ChatSelectors.selectLoading);
    this.error$ = this.store.select(ChatSelectors.selectError);
  }

  ngOnInit(): void {
    this.store.dispatch(ChatActions.loadMessages());
  }

  sendMessage(content: string): void {
    if (content.trim()) {
      this.store.dispatch(ChatActions.sendMessage({ content }));
    }
  }
}
```

```html
<!-- chat.component.html -->
<div class="chat-container">
  <div class="chat-header">
    <h2>MongoDB Chat</h2>
  </div>
  
  <div class="messages-container">
    <app-message-list 
      [messages]="messages$ | async" 
      [loading]="loading$ | async">
    </app-message-list>
    
    <div *ngIf="error$ | async as error" class="error-message">
      {{ error }}
    </div>
  </div>
  
  <app-message-input 
    (sendMessage)="sendMessage($event)"
    [disabled]="loading$ | async">
  </app-message-input>
</div>
```

#### Message List Component

Displays the list of messages in the conversation.

```typescript
// message-list.component.ts
import { Component, Input, OnChanges, SimpleChanges, ElementRef, ViewChild, AfterViewChecked } from '@angular/core';
import { Message } from '../models/message.model';

@Component({
  selector: 'app-message-list',
  templateUrl: './message-list.component.html',
  styleUrls: ['./message-list.component.scss']
})
export class MessageListComponent implements OnChanges, AfterViewChecked {
  @Input() messages: Message[] | null = [];
  @Input() loading = false;
  
  @ViewChild('messageContainer') private messageContainer!: ElementRef;
  
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['messages'] && !changes['messages'].firstChange) {
      this.scrollToBottom();
    }
  }
  
  ngAfterViewChecked(): void {
    this.scrollToBottom();
  }
  
  private scrollToBottom(): void {
    try {
      this.messageContainer.nativeElement.scrollTop = 
        this.messageContainer.nativeElement.scrollHeight;
    } catch (err) { }
  }
}
```

```html
<!-- message-list.component.html -->
<div class="message-list" #messageContainer>
  <div *ngIf="!messages?.length && !loading" class="empty-state">
    <p>No messages yet. Start a conversation!</p>
  </div>
  
  <div *ngFor="let message of messages" class="message" [ngClass]="message.role">
    <div class="message-avatar">
      <div class="avatar" [ngClass]="message.role">
        <mat-icon *ngIf="message.role === 'user'">person</mat-icon>
        <mat-icon *ngIf="message.role === 'assistant'">smart_toy</mat-icon>
      </div>
    </div>
    
    <div class="message-content">
      <div class="message-header">
        <span class="message-sender">{{ message.role === 'user' ? 'You' : 'MongoDB Assistant' }}</span>
        <span class="message-time">{{ message.timestamp | date:'short' }}</span>
      </div>
      
      <div class="message-body">
        <markdown [data]="message.content"></markdown>
      </div>
    </div>
  </div>
  
  <div *ngIf="loading" class="loading-message">
    <div class="message assistant">
      <div class="message-avatar">
        <div class="avatar assistant">
          <mat-icon>smart_toy</mat-icon>
        </div>
      </div>
      
      <div class="message-content">
        <div class="message-header">
          <span class="message-sender">MongoDB Assistant</span>
        </div>
        
        <div class="message-body">
          <ngx-skeleton-loader count="3" appearance="line"></ngx-skeleton-loader>
        </div>
      </div>
    </div>
  </div>
</div>
```

#### Message Input Component

Handles user input for sending messages.

```typescript
// message-input.component.ts
import { Component, Output, EventEmitter, Input } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';

@Component({
  selector: 'app-message-input',
  templateUrl: './message-input.component.html',
  styleUrls: ['./message-input.component.scss']
})
export class MessageInputComponent {
  @Output() sendMessage = new EventEmitter<string>();
  @Input() disabled = false;
  
  messageControl = new FormControl('', [Validators.required]);
  
  onSubmit(): void {
    if (this.messageControl.valid && !this.disabled) {
      this.sendMessage.emit(this.messageControl.value);
      this.messageControl.reset();
    }
  }
}
```

```html
<!-- message-input.component.html -->
<div class="message-input-container">
  <form (ngSubmit)="onSubmit()" class="message-form">
    <mat-form-field appearance="outline" class="message-field">
      <textarea 
        matInput
        [formControl]="messageControl"
        placeholder="Ask a question about MongoDB..."
        cdkTextareaAutosize
        cdkAutosizeMinRows="1"
        cdkAutosizeMaxRows="5"
        [disabled]="disabled">
      </textarea>
    </mat-form-field>
    
    <button 
      mat-fab 
      color="primary" 
      type="submit" 
      [disabled]="messageControl.invalid || disabled">
      <mat-icon>send</mat-icon>
    </button>
  </form>
</div>
```

### History Module

The History module displays past conversations and allows users to revisit them.

#### History Component

```typescript
// history.component.ts
import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { Conversation } from '../models/conversation.model';
import * as HistoryActions from '../store/history.actions';
import * as HistorySelectors from '../store/history.selectors';

@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.scss']
})
export class HistoryComponent implements OnInit {
  conversations$: Observable<Conversation[]>;
  loading$: Observable<boolean>;
  
  constructor(private store: Store) {
    this.conversations$ = this.store.select(HistorySelectors.selectAllConversations);
    this.loading$ = this.store.select(HistorySelectors.selectLoading);
  }
  
  ngOnInit(): void {
    this.store.dispatch(HistoryActions.loadConversations());
  }
  
  openConversation(id: string): void {
    // Navigate to the conversation
  }
  
  deleteConversation(id: string): void {
    this.store.dispatch(HistoryActions.deleteConversation({ id }));
  }
}
```

## Shared Components

### Theme Toggle Component

```typescript
// theme-toggle.component.ts
import { Component, OnInit } from '@angular/core';
import { ThemeService } from '@core/services/theme.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-theme-toggle',
  template: `
    <button mat-icon-button (click)="toggleTheme()" aria-label="Toggle theme">
      <mat-icon>{{ (isDarkTheme$ | async) ? 'light_mode' : 'dark_mode' }}</mat-icon>
    </button>
  `,
  styles: []
})
export class ThemeToggleComponent implements OnInit {
  isDarkTheme$: Observable<boolean>;
  
  constructor(private themeService: ThemeService) {
    this.isDarkTheme$ = this.themeService.isDarkTheme$;
  }
  
  ngOnInit(): void {}
  
  toggleTheme(): void {
    this.themeService.toggleTheme();
  }
}
```

### Loading Spinner Component

```typescript
// loading-spinner.component.ts
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-loading-spinner',
  template: `
    <div class="spinner-container" [class.overlay]="overlay">
      <mat-spinner [diameter]="diameter" [color]="color"></mat-spinner>
    </div>
  `,
  styles: [`
    .spinner-container {
      display: flex;
      justify-content: center;
      align-items: center;
      padding: 20px;
    }
    
    .overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background-color: rgba(255, 255, 255, 0.7);
      z-index: 10;
    }
  `]
})
export class LoadingSpinnerComponent {
  @Input() diameter = 40;
  @Input() color: 'primary' | 'accent' | 'warn' = 'primary';
  @Input() overlay = false;
}
```

## Component Communication

Components communicate through:

1. **Input/Output Properties**: For parent-child communication
2. **Services**: For cross-component communication
3. **NgRx Store**: For application-wide state management

## Styling Strategy

Components use a combination of:

1. **Component-scoped styles**: For component-specific styling
2. **Global styles**: For consistent theming and typography
3. **Angular Material components**: For standard UI elements
4. **CSS variables**: For theme customization

## Accessibility Considerations

All components implement:

1. **ARIA attributes**: For screen reader support
2. **Keyboard navigation**: For non-mouse users
3. **Sufficient color contrast**: For visual accessibility
4. **Focus indicators**: For keyboard users

## Responsive Design

Components use:

1. **Flexbox/Grid layouts**: For responsive positioning
2. **Media queries**: For breakpoint-specific styling
3. **Relative units**: For scalable typography and spacing

## Next Steps

After implementing the component structure:

1. Implement the NgRx store for state management
2. Connect components to backend services
3. Add unit and integration tests
4. Implement accessibility features
5. Add animations for enhanced UX

Refer to the [API Integration Guide](./angular-api-integration.md) for details on connecting components to backend services.
