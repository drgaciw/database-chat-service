import { Message } from '../models/message.model';

export interface ChatState {
  messages: Message[];
  searchResults: Message[];
  messageHistory: Message[];
  loading: boolean;
  searching: boolean;
  loadingHistory: boolean;
  error: string | null;
  selectedMessage: Message | null;
  selectedThread: Message | null;
  threadMessages: Message[];
  typingUsers: Set<string>;
}

export const initialChatState: ChatState = {
  messages: [],
  searchResults: [],
  messageHistory: [],
  loading: false,
  searching: false,
  loadingHistory: false,
  error: null,
  selectedMessage: null,
  selectedThread: null,
  threadMessages: [],
  typingUsers: new Set<string>()
}; 