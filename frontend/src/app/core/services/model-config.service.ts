import { Injectable } from '@angular/core';

export interface ModelConfig {
  name: string;
  temperature: number;
}

@Injectable({
  providedIn: 'root'
})
export class ModelConfigService {

  private readonly creativeModel: ModelConfig = {
    name: 'gemini-2.5-flash',
    temperature: 0.8
  };

  private readonly preciseModel: ModelConfig = {
    name: 'gemini-2.5-flash',
    temperature: 0.2
  };

  constructor() { }

  getModelConfig(role: 'Creative' | 'Precise'): ModelConfig {
    switch (role) {
      case 'Creative':
        return this.creativeModel;
      case 'Precise':
        return this.preciseModel;
      default:
        return this.preciseModel;
    }
  }
}
