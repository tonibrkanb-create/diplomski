import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, Subject, map, tap } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Korisnik {
  id: number;
  ime: string;
  prezime: string;
  email: string;
  telefon?: string;
  tvrtka?: string;
  adresa?: string;
  mjesto?: string;
  postanskiBroj?: string;
  drzava?: string;
  isActive: boolean;
  createdAt: string;
}

export interface Ponuda {
  id: number;
  korisnikId: number;
  opis: string;
  vrstaAtesta?: string;
  lokacija?: string;
  zeljeniDatum?: string;
  status: 'nova' | 'poslana' | 'odobrena' | 'odbijena';
  odgovor?: string;
  createdAt: string;
}

export interface Obavijest {
  id: number;
  korisnikId: number;
  naslov: string;
  poruka: string;
  procitana: boolean;
  createdAt: string;
}

export interface Recenzija {
  id: number;
  korisnikId: number;
  radniNalogId?: number;
  ocjena: number;
  komentar?: string;
  odgovor?: string;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class KorisnikService {
  private readonly http = inject(HttpClient);
  private readonly tokenKey = 'korisnik_token';
  readonly unauthorizedEvent$ = new Subject<void>();

  private getHeaders(): HttpHeaders {
    const token = this.getToken();
    return new HttpHeaders({ Authorization: `Bearer ${token}` });
  }

  // Auth
  register(data: { ime: string; prezime: string; email: string; password: string; telefon?: string; tvrtka?: string }): Observable<{ korisnik: Korisnik; token: string }> {
    return this.http.post<{ korisnik: Korisnik; token: string }>(`${environment.apiUrl}/korisnik/register`, data)
      .pipe(tap(res => this.setToken(res.token)));
  }

  login(email: string, password: string): Observable<{ korisnik: Korisnik; token: string }> {
    return this.http.post<{ korisnik: Korisnik; token: string }>(`${environment.apiUrl}/korisnik/login`, { email, password })
      .pipe(tap(res => this.setToken(res.token)));
  }

  // Profile
  getProfile(): Observable<Korisnik> {
    return this.http.get<Korisnik>(`${environment.apiUrl}/korisnik/profil`, { headers: this.getHeaders() });
  }

  updateProfile(data: Partial<Korisnik>): Observable<Korisnik> {
    return this.http.put<Korisnik>(`${environment.apiUrl}/korisnik/profil`, data, { headers: this.getHeaders() });
  }

  // Ponude
  getPonude(): Observable<Ponuda[]> {
    return this.http.get<Ponuda[]>(`${environment.apiUrl}/korisnik/ponude`, { headers: this.getHeaders() });
  }

  getPonuda(id: number): Observable<Ponuda> {
    return this.http.get<Ponuda>(`${environment.apiUrl}/korisnik/ponude/${id}`, { headers: this.getHeaders() });
  }

  createPonuda(data: { opis: string; vrstaAtesta?: string; lokacija?: string; zeljeniDatum?: string }): Observable<Ponuda> {
    return this.http.post<Ponuda>(`${environment.apiUrl}/korisnik/ponude`, data, { headers: this.getHeaders() });
  }

  // Obavijesti
  getObavijesti(): Observable<Obavijest[]> {
    return this.http.get<Obavijest[]>(`${environment.apiUrl}/korisnik/obavijesti`, { headers: this.getHeaders() });
  }

  markAsRead(id: number): Observable<Obavijest> {
    return this.http.put<Obavijest>(`${environment.apiUrl}/korisnik/obavijesti/${id}/procitaj`, {}, { headers: this.getHeaders() });
  }

  // Recenzije
  getRecenzije(): Observable<Recenzija[]> {
    return this.http.get<Recenzija[]>(`${environment.apiUrl}/korisnik/recenzije`, { headers: this.getHeaders() });
  }

  getRecenzija(id: number): Observable<Recenzija> {
    return this.http.get<Recenzija>(`${environment.apiUrl}/korisnik/recenzije/${id}`, { headers: this.getHeaders() });
  }

  createRecenzija(data: { ocjena: number; komentar?: string; radniNalogId?: number }): Observable<Recenzija> {
    return this.http.post<Recenzija>(`${environment.apiUrl}/korisnik/recenzije`, data, { headers: this.getHeaders() });
  }

  // Token management
  getToken(): string | null {
    if (typeof window === 'undefined') return null;
    return localStorage.getItem(this.tokenKey);
  }

  setToken(token: string): void {
    if (typeof window === 'undefined') return;
    localStorage.setItem(this.tokenKey, token.trim());
  }

  clearToken(): void {
    if (typeof window === 'undefined') return;
    localStorage.removeItem(this.tokenKey);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    this.clearToken();
    this.unauthorizedEvent$.next();
  }
}
