export interface Message {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  timestamp: Date;
  parentId?: string;
  isEdited?: boolean;
  editedAt?: Date;
  replyCount?: number;
  deleted?: boolean;
  searchScore?: number;
  context?: {
    before?: string[];
    after?: string[];
  };
}

export interface Reference {
  title: string;
  url: string;
  snippet?: string;
}

export interface ChatState {
  messages: Message[];
  loading: boolean;
  error: string | null;
  references: Reference[];
  isStreaming: boolean;
  searchResults?: Message[];
  messageHistory?: Message[];
  selectedThread?: Message | null;
  threadMessages?: Message[];
  typingUsers?: string[];
  selectedMessage?: Message | null;
  hasMore?: boolean;
}
