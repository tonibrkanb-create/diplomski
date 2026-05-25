import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, firstValueFrom } from 'rxjs';

export interface AppConfig {
  production: boolean;
  apiUrl: string;
  [key: string]: any;
}

@Injectable({
  providedIn: 'root'
})
export class ConfigService {
  private configSubject = new BehaviorSubject<AppConfig | null>(null);
  public config$ = this.configSubject.asObservable();

  constructor(private http: HttpClient) {}

  async loadConfig(): Promise<AppConfig> {
    try {
      const config = await firstValueFrom(
        this.http.get<AppConfig>('/config.json')
      );
      this.configSubject.next(config);
      return config;
    } catch (error) {
      console.error('Failed to load config.json', error);
      throw error;
    }
  }

  getConfig(): AppConfig | null {
    return this.configSubject.value;
  }

  get apiUrl(): string {
    return this.configSubject.value?.apiUrl || '';
  }

  get production(): boolean {
    return this.configSubject.value?.production || false;
  }

  get<T>(key: string, defaultValue?: T): T {
    return this.configSubject.value?.[key] ?? defaultValue;
  }
}
