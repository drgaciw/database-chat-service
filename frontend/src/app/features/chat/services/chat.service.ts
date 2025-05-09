import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, OnDestroy } from '@angular/core';
import { environment } from '@env/environment';
import { Observable, Subject, fromEvent, map, merge, takeUntil } from 'rxjs';
import { Message } from '../models/message.model';

@Injectable({
  providedIn: 'root'
})
export class ChatService implements OnDestroy {
  private readonly apiUrl = `${environment.apiUrl}/chat`;
  private eventSource: EventSource | null = null;
  private destroy$ = new Subject<void>();
  private typingUsers = new Set<string>();
  private typingSubject = new Subject<Set<string>>();

  constructor(private http: HttpClient) {}

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.closeEventSource();
  }

  getMessages(parentId?: string, page = 1, limit = 50): Observable<Message[]> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('limit', limit.toString());

    if (parentId) {
      params = params.set('parentId', parentId);
    }

    return this.http.get<Message[]>(`${this.apiUrl}/messages`, { params });
  }

  searchMessages(query: string, page = 1, limit = 20): Observable<Message[]> {
    const params = new HttpParams()
      .set('query', query)
      .set('page', page.toString())
      .set('limit', limit.toString());

    return this.http.get<Message[]>(`${this.apiUrl}/messages/search`, { params });
  }

  getMessageHistory(messageId: string, limit = 5): Observable<Message[]> {
    const params = new HttpParams()
      .set('limit', limit.toString());

    return this.http.get<Message[]>(`${this.apiUrl}/messages/${messageId}/history`, { params });
  }

  sendMessage(content: string, parentId?: string): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/send`, { content, parentId });
  }

  editMessage(messageId: string, content: string): Observable<Message> {
    return this.http.put<Message>(`${this.apiUrl}/messages/${messageId}`, { content });
  }

  deleteMessage(messageId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/messages/${messageId}`);
  }

  getReplies(messageId: string): Observable<Message[]> {
    return this.http.get<Message[]>(`${this.apiUrl}/messages/${messageId}/replies`);
  }

  startTyping(): void {
    this.http.post(`${this.apiUrl}/typing/start`, {}).subscribe();
  }

  stopTyping(): void {
    this.http.post(`${this.apiUrl}/typing/stop`, {}).subscribe();
  }

  connectToEvents(): Observable<Message | { type: string; data: any }> {
    this.closeEventSource();
    this.eventSource = new EventSource(`${this.apiUrl}/events`);

    const messageEvents$ = fromEvent(this.eventSource, 'message').pipe(
      map((event: Event) => {
        const messageEvent = event as MessageEvent;
        return JSON.parse(messageEvent.data) as Message;
      })
    );

    const typingEvents$ = fromEvent(this.eventSource, 'typing').pipe(
      map((event: Event) => {
        const typingEvent = event as MessageEvent;
        const data = JSON.parse(typingEvent.data);
        if (data.isTyping) {
          this.typingUsers.add(data.userId);
        } else {
          this.typingUsers.delete(data.userId);
        }
        this.typingSubject.next(this.typingUsers);
        return { type: 'typing', data };
      })
    );

    const editEvents$ = fromEvent(this.eventSource, 'messageEdit').pipe(
      map((event: Event) => {
        const editEvent = event as MessageEvent;
        return { type: 'edit', data: JSON.parse(editEvent.data) };
      })
    );

    const deleteEvents$ = fromEvent(this.eventSource, 'messageDelete').pipe(
      map((event: Event) => {
        const deleteEvent = event as MessageEvent;
        return { type: 'delete', data: JSON.parse(deleteEvent.data) };
      })
    );

    const errorEvents$ = fromEvent(this.eventSource, 'error').pipe(
      map((event: Event) => {
        console.error('SSE Error:', event);
        return { type: 'error', data: event };
      })
    );

    return merge(
      messageEvents$,
      typingEvents$,
      editEvents$,
      deleteEvents$,
      errorEvents$
    ).pipe(takeUntil(this.destroy$));
  }

  getTypingUsers(): Observable<Set<string>> {
    return this.typingSubject.asObservable();
  }

  private closeEventSource(): void {
    if (this.eventSource) {
      this.eventSource.close();
      this.eventSource = null;
    }
  }
}
