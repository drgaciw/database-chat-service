import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Message } from '../models/message.model';

@Injectable({
  providedIn: 'root'
})
export class MockChatService {
  private mockMessages: Message[] = [
    {
      id: '1',
      content: 'Test message 1',
      role: 'user',
      timestamp: new Date()
    },
    {
      id: '2',
      content: 'Test response 1',
      role: 'assistant',
      timestamp: new Date()
    }
  ];

  getMessages(): Observable<Message[]> {
    return of(this.mockMessages);
  }

  sendMessage(content: string): Observable<Message> {
    const newMessage: Message = {
      id: Date.now().toString(),
      content,
      role: 'user',
      timestamp: new Date()
    };
    this.mockMessages.push(newMessage);
    return of(newMessage);
  }
} 