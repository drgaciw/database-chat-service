import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Message } from 'src/app/features/chat/models/message.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AiService {

  private readonly apiUrl = `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${environment.googleApiKey}`;
  private readonly streamApiUrl = `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:streamGenerateContent?alt=sse&key=${environment.googleApiKey}`;

  constructor(private http: HttpClient) { }

  generateContent(prompt: string, history: Message[] = []): Observable<string> {
    const contents = [
      ...history.map(message => ({
        role: message.role,
        parts: [{ text: message.content }]
      })),
      {
        role: 'user',
        parts: [{ text: prompt }]
      }
    ];

    const body = {
      contents
    };

    return this.http.post<any>(this.apiUrl, body).pipe(
      map(response => response.candidates[0].content.parts[0].text)
    );
  }

  generateContentStream(prompt: string, history: Message[] = []): Observable<string> {
    const contents = [
      ...history.map(message => ({
        role: message.role,
        parts: [{ text: message.content }]
      })),
      {
        role: 'user',
        parts: [{ text: prompt }]
      }
    ];

    const body = {
      contents
    };

    return new Observable<string>(observer => {
      const controller = new AbortController();

      fetch(this.streamApiUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(body),
        signal: controller.signal
      }).then(response => {
        if (!response.ok) {
          return response.json().then(errorBody => {
            const errorMessage = errorBody.error?.message || 'Unknown API error';
            throw new Error(errorMessage);
          });
        }
        if (!response.body) {
          throw new Error('No response body');
        }
        const reader = response.body.getReader();
        const decoder = new TextDecoder();

        function push() {
          reader.read().then(({ done, value }) => {
            if (done) {
              observer.complete();
              return;
            }
            const chunk = decoder.decode(value);
            const lines = chunk.split('\n');
            for (const line of lines) {
              if (line.startsWith('data: ')) {
                try {
                  const json = JSON.parse(line.substring(6));
                  if (json.candidates && json.candidates[0].content.parts[0].text) {
                    observer.next(json.candidates[0].content.parts[0].text);
                  }
                } catch (e) {
                  // Ignore parsing errors, as some chunks might be incomplete
                }
              }
            }
            push();
          }).catch(err => {
            observer.error(err);
          });
        }
        push();
      }).catch(err => {
        observer.error(err);
      });

      return () => {
        controller.abort();
      };
    });
  }
}
