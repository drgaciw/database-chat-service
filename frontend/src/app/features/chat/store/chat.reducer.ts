import { createReducer, on } from '@ngrx/store';
import { ChatState } from '../models/message.model';
import * as ChatActions from './chat.actions';

export const chatFeatureKey = 'chat';

export const initialState: ChatState = {
  messages: [],
  loading: false,
  error: null,
  references: [],
  isStreaming: false,
  searchResults: [],
  messageHistory: [],
  selectedThread: null,
  threadMessages: [],
  typingUsers: [],
  selectedMessage: null,
  hasMore: true
};

export const chatReducer = createReducer(
  initialState,



  // Load messages
  on(ChatActions.loadMessages, state => ({
    ...state,
    loading: true,
    error: null
  })),
  on(ChatActions.loadMessagesSuccess, (state, { messages }) => ({
    ...state,
    messages: messages,
    loading: false
  })),
  on(ChatActions.loadMessagesFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),

  // Search messages
  on(ChatActions.searchMessages, state => ({
    ...state,
    searching: true,
    error: null
  })),
  on(ChatActions.searchMessagesSuccess, (state, { results }) => ({
    ...state,
    searchResults: results,
    searching: false
  })),
  on(ChatActions.searchMessagesFailure, (state, { error }) => ({
    ...state,
    searching: false,
    error
  })),

  // Message history
  on(ChatActions.loadMessageHistory, state => ({
    ...state,
    loadingHistory: true,
    error: null
  })),
  on(ChatActions.loadMessageHistorySuccess, (state, { history }) => ({
    ...state,
    messageHistory: history,
    loadingHistory: false
  })),
  on(ChatActions.loadMessageHistoryFailure, (state, { error }) => ({
    ...state,
    loadingHistory: false,
    error
  })),

  // Send message
  on(ChatActions.sendMessage, state => ({
    ...state,
    loading: true,
    error: null
  })),
  on(ChatActions.sendMessageSuccess, (state, { message }) => ({
    ...state,
    messages: [...state.messages, message],
    loading: false
  })),
  on(ChatActions.sendMessageFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),

  // Thread management
  on(ChatActions.openThread, (state, { message }) => ({
    ...state,
    selectedThread: message
  })),
  on(ChatActions.closeThread, state => ({
    ...state,
    selectedThread: null,
    threadMessages: []
  })),
  on(ChatActions.loadThreadMessages, state => ({
    ...state,
    loading: true,
    error: null
  })),
  on(ChatActions.loadThreadMessagesSuccess, (state, { messages }) => ({
    ...state,
    threadMessages: messages,
    loading: false
  })),
  on(ChatActions.loadThreadMessagesFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),

  // Typing indicators
  on(ChatActions.updateTypingUsers, (state, { users }) => ({
    ...state,
    typingUsers: Array.from(users)
  })),

  // Message selection
  on(ChatActions.selectMessage, (state, { message }) => ({
    ...state,
    selectedMessage: message
  })),
  on(ChatActions.clearSelectedMessage, state => ({
    ...state,
    selectedMessage: null,
    messageHistory: []
  })),

  // Error handling
  on(ChatActions.clearError, state => ({
    ...state,
    error: null
  }))
);
