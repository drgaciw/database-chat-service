import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Message } from 'src/app/features/chat/models/message.model';
import { environment } from 'src/environments/environment';
import { ModelConfig } from './model-config.service';

@Injectable({
  providedIn: 'root'
})
export class AiService {

  constructor(private http: HttpClient) { }

  generateContent(prompt: string, history: Message[] = [], modelConfig?: ModelConfig): Observable<string> {
    const apiUrl = `https://generativelanguage.googleapis.com/v1beta/models/${modelConfig?.name || 'gemini-2.5-flash'}:generateContent?key=${environment.googleApiKey}`;
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
      contents,
      generationConfig: {
        temperature: modelConfig?.temperature || 0.2
      }
    };

    return this.http.post<any>(apiUrl, body).pipe(
      map(response => response.candidates[0].content.parts[0].text)
    );
  }

  generateContentStream(prompt: string, history: Message[] = [], modelConfig?: ModelConfig): Observable<string> {
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
      contents,
      generationConfig: {
        temperature: modelConfig?.temperature || 0.2
      }
    };

    return new Observable<string>(observer => {
      const controller = new AbortController();
      const streamApiUrl = `https://generativelanguage.googleapis.com/v1beta/models/${modelConfig?.name || 'gemini-2.5-flash'}:streamGenerateContent?alt=sse&key=${environment.googleApiKey}`;

      fetch(streamApiUrl, {
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
