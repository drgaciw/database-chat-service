import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Store } from '@ngrx/store';
import { Subject, debounceTime, distinctUntilChanged, takeUntil } from 'rxjs';
import { ChatState, Message } from '../../models/message.model';
import { ChatService } from '../../services/chat.service';
import * as ChatActions from '../../store/chat.actions';
import * as ChatSelectors from '../../store/chat.selectors';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.scss']
})
export class ChatComponent implements OnInit, OnDestroy {
  @ViewChild('messagesContainer') private messagesContainer!: ElementRef;

  messageForm: FormGroup;
  searchForm: FormGroup;
  private destroy$ = new Subject<void>();
  private typingTimeout: any;

  // Observable state
  messages$ = this.store.select(ChatSelectors.selectAllMessages);
  searchResults$ = this.store.select(ChatSelectors.selectSearchResults);
  messageHistory$ = this.store.select(ChatSelectors.selectMessageHistory);
  loading$ = this.store.select(ChatSelectors.selectLoading);
  isStreaming$ = this.store.select(ChatSelectors.selectIsStreaming);
  error$ = this.store.select(ChatSelectors.selectError);
  selectedMessage$ = this.store.select(ChatSelectors.selectSelectedMessage);
  selectedThread$ = this.store.select(ChatSelectors.selectSelectedThread);
  threadMessages$ = this.store.select(ChatSelectors.selectThreadMessages);
  typingUsers$ = this.store.select(ChatSelectors.selectTypingUsers);
  hasMore$ = this.store.select(ChatSelectors.selectHasMoreMessages);
  references$ = this.store.select(ChatSelectors.selectReferences);

  constructor(
    private fb: FormBuilder,
    private store: Store<{ chat: ChatState }>,
    private chatService: ChatService,
    private snackBar: MatSnackBar
  ) {
    this.messageForm = this.fb.group({
      message: ['', [Validators.required, Validators.minLength(1)]]
    });

    this.searchForm = this.fb.group({
      query: ['', [Validators.required, Validators.minLength(1)]]
    });
  }

  ngOnInit(): void {
    this.loadMessages();
    this.setupRealtimeConnection();
    this.setupTypingDetection();
    this.setupSearch();
    this.setupErrorHandling();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    if (this.typingTimeout) {
      clearTimeout(this.typingTimeout);
    }
  }

  private loadMessages(page = 1): void {
    this.store.dispatch(ChatActions.loadMessages({ page }));
  }

  private setupRealtimeConnection(): void {
    this.chatService.connectToEvents()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (event: any) => {
          if ('type' in event) {
            switch (event.type) {
              case 'typing':
                this.handleTypingEvent(event.data);
                break;
              case 'edit':
                this.handleMessageEdit(event.data);
                break;
              case 'delete':
                this.handleMessageDelete(event.data);
                break;
              case 'error':
                console.error('SSE Error:', event.data);
                break;
            }
          }
        },
        error: (err) => {
          console.error('SSE Connection Error:', err);
          this.store.dispatch(ChatActions.loadMessagesFailure({ error: 'Lost connection to chat server' }));
        }
      });
  }

  private setupTypingDetection(): void {
    this.messageForm.get('message')?.valueChanges
      .pipe(
        takeUntil(this.destroy$),
        debounceTime(300),
        distinctUntilChanged()
      )
      .subscribe(value => {
        if (value?.length > 0) {
          this.store.dispatch(ChatActions.startTyping());
          if (this.typingTimeout) {
            clearTimeout(this.typingTimeout);
          }
          this.typingTimeout = setTimeout(() => {
            this.store.dispatch(ChatActions.stopTyping());
          }, 2000);
        } else {
          this.store.dispatch(ChatActions.stopTyping());
        }
      });
  }

  private setupSearch(): void {
    this.searchForm.get('query')?.valueChanges
      .pipe(
        takeUntil(this.destroy$),
        debounceTime(300),
        distinctUntilChanged()
      )
      .subscribe(query => {
        if (query?.length > 0) {
          this.store.dispatch(ChatActions.searchMessages({ query }));
        }
      });
  }

  private setupErrorHandling(): void {
    this.error$
      .pipe(takeUntil(this.destroy$))
      .subscribe(error => {
        if (error) {
          this.snackBar.open(error, 'Dismiss', { duration: 5000 });
          this.store.dispatch(ChatActions.clearError());
        }
      });
  }

  searchMessages(query: string, page = 1): void {
    this.store.dispatch(ChatActions.searchMessages({ query, page }));
  }

  loadMoreMessages(): void {
    this.store.dispatch(ChatActions.loadMessages({ page: 2 }));
  }

  showMessageHistory(message: Message): void {
    if (!message.id) return;
    this.store.dispatch(ChatActions.selectMessage({ message }));
    this.store.dispatch(ChatActions.loadMessageHistory({ messageId: message.id }));
  }

  closeMessageHistory(): void {
    this.store.dispatch(ChatActions.clearSelectedMessage());
  }

  sendMessage(): void {
    if (this.messageForm.invalid) return;

    const message = this.messageForm.get('message')?.value;
    this.messageForm.reset();

    this.store.dispatch(ChatActions.sendMessage({ content: message }));
  }

  onEnter(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.sendMessage();
    }
  }

  openThread(message: Message): void {
    if (message.id) {
      this.store.dispatch(ChatActions.openThread({ message }));
      this.store.dispatch(ChatActions.loadThreadMessages({ messageId: message.id }));
    }
  }

  closeThread(): void {
    this.store.dispatch(ChatActions.closeThread());
  }

  private handleTypingEvent(data: { userId: string; isTyping: boolean }): void {
    const usersSet = new Set<string>();
    if (data.isTyping) {
      usersSet.add(data.userId);
    }
    const users = Array.from(usersSet);
    this.store.dispatch(ChatActions.updateTypingUsers({ users }));
  }

  private handleMessageEdit(_data: Message): void {
    // Handle message edit in the store
    // Implementation will be added later
  }

  private handleMessageDelete(_data: { messageId: string }): void {
    // Handle message delete in the store
    // Implementation will be added later
  }

  // Scroll to the bottom of the messages container
  scrollToBottom(): void {
    setTimeout(() => {
      if (this.messagesContainer) {
        this.messagesContainer.nativeElement.scrollTop =
          this.messagesContainer.nativeElement.scrollHeight;
      }
    }, 100);
  }
}
