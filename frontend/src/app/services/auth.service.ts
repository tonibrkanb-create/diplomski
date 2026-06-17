import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject, map, tap } from 'rxjs';
import { environment } from '../../environments/environment';

export interface LoginResponse {
  token?: string;
  accessToken?: string;
  access_token?: string;
  jwt?: string;
  role?: string;
  data?: {
    token?: string;
    accessToken?: string;
    access_token?: string;
    jwt?: string;
    role?: string;
  };
  user?: {
    role?: string;
  };
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly tokenStorageKey = 'auth_token';
  private readonly roleStorageKey = 'auth_role';
  private readonly fallbackTokenKeys = ['auth_token', 'token', 'access_token', 'accessToken', 'jwt'];
  private readonly fallbackRoleKeys = ['auth_role', 'role'];

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
        tap((response) => {
          const token = this.extractToken(response);
          const role = this.extractRole(response);
          this.setToken(token);
          this.setRole(role);
        }),
        map((response) => this.extractToken(response))
      );
  }

  korisnikLogin(email: string, password: string): Observable<string> {
    console.log('AuthService: Initiating login for', email);
    return this.http
      .post<LoginResponse>(`${environment.apiUrl}/auth/korisnik-login`, { email, password })
      .pipe(
        tap((response) => {
          const token = this.extractToken(response);
          this.setToken(token);
          this.clearRole();
        }),
        map((response) => this.extractToken(response))
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

    this.clearRole();
  }

  getRole(): string | null {
    if (typeof window === 'undefined') {
      return null;
    }

    for (const key of this.fallbackRoleKeys) {
      const localRole = localStorage.getItem(key);
      if (localRole) {
        if (key !== this.roleStorageKey) {
          localStorage.setItem(this.roleStorageKey, localRole);
        }
        return localRole;
      }

      const sessionRole = sessionStorage.getItem(key);
      if (sessionRole) {
        localStorage.setItem(this.roleStorageKey, sessionRole);
        return sessionRole;
      }
    }

    return null;
  }

  setRole(role: string | null | undefined): void {
    if (typeof window === 'undefined') {
      return;
    }

    const sanitizedRole = role?.trim();
    if (!sanitizedRole) {
      this.clearRole();
      return;
    }

    for (const key of this.fallbackRoleKeys) {
      localStorage.setItem(key, sanitizedRole);
    }
    sessionStorage.setItem(this.roleStorageKey, sanitizedRole);
  }

  clearRole(): void {
    if (typeof window === 'undefined') {
      return;
    }

    for (const key of this.fallbackRoleKeys) {
      localStorage.removeItem(key);
      sessionStorage.removeItem(key);
    }
  }

  private extractToken(response: LoginResponse): string {
    console.log('AuthService: Extracting token from response', response);
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

  private extractRole(response: LoginResponse): string | null {
    const role = response?.role
      ?? response?.data?.role
      ?? response?.user?.role;

    return role?.trim() || null;
  }
}