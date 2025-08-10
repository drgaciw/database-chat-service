import { Injectable } from '@angular/core';

export interface PromptTemplate {
  name: string;
  text: string;
}

@Injectable({
  providedIn: 'root'
})
export class PromptService {

  private readonly templates: PromptTemplate[] = [
    {
      name: 'Summarize Text',
      text: 'Summarize the following text:\n\n'
    },
    {
      name: 'Translate to French',
      text: 'Translate the following text to French:\n\n'
    },
    {
      name: 'Explain Like I\'m 5',
      text: 'Explain the following concept like I\'m 5 years old:\n\n'
    }
  ];

  constructor() { }

  /**
   * Gets the list of predefined prompt templates.
   * @returns An array of prompt templates.
   */
  getTemplates(): PromptTemplate[] {
    return this.templates;
  }
}
