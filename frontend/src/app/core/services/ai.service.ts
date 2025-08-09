import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AiService {

  private readonly apiUrl = `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${environment.googleApiKey}`;

  constructor(private http: HttpClient) { }

  generateContent(prompt: string): Observable<string> {
    const body = {
      contents: [
        {
          parts: [
            {
              text: prompt
            }
          ]
        }
      ]
    };

    return this.http.post<any>(this.apiUrl, body).pipe(
      map(response => response.candidates[0].content.parts[0].text)
    );
  }
}
