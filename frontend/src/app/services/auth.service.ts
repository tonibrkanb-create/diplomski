import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject, map, tap } from 'rxjs';
import { environment } from '../../environments/environment';

export interface LoginResponse {
  token?: string;
  accessToken?: string;
  access_token?: string;
  jwt?: string;
  data?: {
    token?: string;
    accessToken?: string;
    access_token?: string;
    jwt?: string;
  };
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly tokenStorageKey = 'auth_token';
  private readonly fallbackTokenKeys = ['auth_token', 'token', 'access_token', 'accessToken', 'jwt'];

  /** Emits when a 401 response is received and the session should be ended. */
  readonly unauthorizedEvent$ = new Subject<void>();

  notifyUnauthorized(): void {
    this.clearToken();
    this.unauthorizedEvent$.next();
  }

  login(username: string, password: string): Observable<string> {
    console.log('AuthService: Initiating login for', username);
    return this.http
      .post<LoginResponse>(`${environment.apiUrl}/auth/login`, { username, password })
      .pipe(
        map((response) => this.extractToken(response)),
        tap((token) => this.setToken(token))
      );
  }

  getToken(): string | null {
    if (typeof window === 'undefined') {
      return null;
    }

    for (const key of this.fallbackTokenKeys) {
      const localToken = localStorage.getItem(key);
      if (localToken) {
        if (key !== this.tokenStorageKey) {
          localStorage.setItem(this.tokenStorageKey, localToken);
        }
        return localToken;
      }

      const sessionToken = sessionStorage.getItem(key);
      if (sessionToken) {
        localStorage.setItem(this.tokenStorageKey, sessionToken);
        return sessionToken;
      }
    }

    return null;
  }

  setToken(token: string): void {
    if (typeof window === 'undefined') {
      return;
    }

    const sanitizedToken = token.trim();
    for (const key of this.fallbackTokenKeys) {
      localStorage.setItem(key, sanitizedToken);
    }
    sessionStorage.setItem(this.tokenStorageKey, sanitizedToken);
  }

  clearToken(): void {
    if (typeof window === 'undefined') {
      return;
    }

    for (const key of this.fallbackTokenKeys) {
      localStorage.removeItem(key);
      sessionStorage.removeItem(key);
    }
  }

  private extractToken(response: LoginResponse): string {
    const token = response?.token
      ?? response?.accessToken
      ?? response?.access_token
      ?? response?.jwt
      ?? response?.data?.token
      ?? response?.data?.accessToken
      ?? response?.data?.access_token
      ?? response?.data?.jwt;

    if (!token) {
      throw new Error('Token not found in login response');
    }

    return token;
  }
}