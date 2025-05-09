import { createFeatureSelector, createSelector } from '@ngrx/store';
import { ChatState } from '../models/message.model';

export const selectChatState = createFeatureSelector<ChatState>('chat');

export const selectAllMessages = createSelector(
  selectChatState,
  (state: ChatState) => state.messages
);

export const selectSearchResults = createSelector(
  selectChatState,
  (state: ChatState) => state.searchResults
);

export const selectMessageHistory = createSelector(
  selectChatState,
  (state: ChatState) => state.messageHistory
);

export const selectLoading = createSelector(
  selectChatState,
  (state: ChatState) => state.loading
);

export const selectIsStreaming = createSelector(
  selectChatState,
  (state: ChatState) => state.isStreaming
);



export const selectError = createSelector(
  selectChatState,
  (state: ChatState) => state.error
);

export const selectSelectedMessage = createSelector(
  selectChatState,
  (state: ChatState) => state.selectedMessage
);

export const selectSelectedThread = createSelector(
  selectChatState,
  (state: ChatState) => state.selectedThread
);

export const selectThreadMessages = createSelector(
  selectChatState,
  (state: ChatState) => state.threadMessages
);

export const selectTypingUsers = createSelector(
  selectChatState,
  (state: ChatState) => state.typingUsers
);

export const selectHasMoreMessages = createSelector(
  selectChatState,
  (state: ChatState) => state.hasMore || false
);

export const selectReferences = createSelector(
  selectChatState,
  (state: ChatState) => state.references
);
