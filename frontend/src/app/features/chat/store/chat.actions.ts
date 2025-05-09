import { createAction, props } from '@ngrx/store';
import { Conversation } from '../models/conversation.model';
import { Message } from '../models/message.model';

// Load conversations
export const loadConversations = createAction('[Chat] Load Conversations');
export const loadConversationsSuccess = createAction(
  '[Chat] Load Conversations Success',
  props<{ conversations: Conversation[] }>()
);
export const loadConversationsFailure = createAction(
  '[Chat] Load Conversations Failure',
  props<{ error: string }>()
);

// Create conversation
export const createConversation = createAction(
  '[Chat] Create Conversation',
  props<{ title: string }>()
);
export const createConversationSuccess = createAction(
  '[Chat] Create Conversation Success',
  props<{ conversation: Conversation }>()
);
export const createConversationFailure = createAction(
  '[Chat] Create Conversation Failure',
  props<{ error: string }>()
);

// Load messages
export const loadMessages = createAction(
  '[Chat] Load Messages',
  props<{ parentId?: string; page?: number }>()
);
export const loadMessagesSuccess = createAction(
  '[Chat] Load Messages Success',
  props<{ messages: Message[]; hasMore: boolean }>()
);
export const loadMessagesFailure = createAction(
  '[Chat] Load Messages Failure',
  props<{ error: string }>()
);

// Search messages
export const searchMessages = createAction(
  '[Chat] Search Messages',
  props<{ query: string; page?: number }>()
);
export const searchMessagesSuccess = createAction(
  '[Chat] Search Messages Success',
  props<{ results: Message[] }>()
);
export const searchMessagesFailure = createAction(
  '[Chat] Search Messages Failure',
  props<{ error: string }>()
);

// Message history
export const loadMessageHistory = createAction(
  '[Chat] Load Message History',
  props<{ messageId: string }>()
);
export const loadMessageHistorySuccess = createAction(
  '[Chat] Load Message History Success',
  props<{ history: Message[] }>()
);
export const loadMessageHistoryFailure = createAction(
  '[Chat] Load Message History Failure',
  props<{ error: string }>()
);

// Send message
export const sendMessage = createAction(
  '[Chat] Send Message',
  props<{ content: string; parentId?: string }>()
);
export const sendMessageSuccess = createAction(
  '[Chat] Send Message Success',
  props<{ message: Message }>()
);
export const sendMessageFailure = createAction(
  '[Chat] Send Message Failure',
  props<{ error: string }>()
);

// Thread management
export const openThread = createAction(
  '[Chat] Open Thread',
  props<{ message: Message }>()
);
export const closeThread = createAction('[Chat] Close Thread');
export const loadThreadMessages = createAction(
  '[Chat] Load Thread Messages',
  props<{ messageId: string }>()
);
export const loadThreadMessagesSuccess = createAction(
  '[Chat] Load Thread Messages Success',
  props<{ messages: Message[] }>()
);
export const loadThreadMessagesFailure = createAction(
  '[Chat] Load Thread Messages Failure',
  props<{ error: string }>()
);

// Typing indicators
export const startTyping = createAction('[Chat] Start Typing');
export const stopTyping = createAction('[Chat] Stop Typing');
export const updateTypingUsers = createAction(
  '[Chat] Update Typing Users',
  props<{ users: string[] }>()
);

// Message selection
export const selectMessage = createAction(
  '[Chat] Select Message',
  props<{ message: Message }>()
);
export const clearSelectedMessage = createAction('[Chat] Clear Selected Message');

// Error handling
export const clearError = createAction('[Chat] Clear Error');
