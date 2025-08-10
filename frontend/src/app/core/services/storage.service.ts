import { Injectable } from '@angular/core';
import { Message } from 'src/app/features/chat/models/message.model';

@Injectable({
  providedIn: 'root'
})
export class StorageService {

  private readonly chatHistoryKey = 'chat_history';

  constructor() { }

  saveChatHistory(history: Message[]): void {
    try {
      localStorage.setItem(this.chatHistoryKey, JSON.stringify(history));
    } catch (e) {
      console.error('Error saving chat history to localStorage', e);
    }
  }

  loadChatHistory(): Message[] | null {
    try {
      const historyJson = localStorage.getItem(this.chatHistoryKey);
      if (historyJson) {
        return JSON.parse(historyJson);
      }
      return null;
    } catch (e) {
      console.error('Error loading chat history from localStorage', e);
      return null;
    }
  }
}
