import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';

export interface Aktivnost {
  id?: number;
  naziv: string;
  trajanje: number;
  cijena?: number | null;
  isActive: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class AktivnostiService {
  private apiUrl = `${environment.apiUrl}/aktivnosti`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Aktivnost[]> {
    return this.http.get<unknown[]>(this.apiUrl, {
      headers: { 'Cache-Control': 'no-cache', 'Pragma': 'no-cache' },
      params: { t: Date.now().toString() }
    }).pipe(
      map((items) => {
        console.log('AKTIVNOSTI RAW RESPONSE:', items);
        return (items ?? []).map((item) => this.normalize(item));
      })
    );
  }

  getById(id: number): Observable<Aktivnost> {
    return this.http.get<unknown>(`${this.apiUrl}/${id}`, {
      headers: { 'Cache-Control': 'no-cache', 'Pragma': 'no-cache' },
      params: { t: Date.now().toString() }
    }).pipe(map((item) => this.normalize(item)));
  }

  create(payload: Aktivnost): Observable<Aktivnost> {
    return this.http.post<unknown>(this.apiUrl, this.toApiPayload(payload)).pipe(map((item) => this.normalize(item)));
  }

  update(id: number, payload: Aktivnost): Observable<Aktivnost> {
    return this.http.put<unknown>(`${this.apiUrl}/${id}`, this.toApiPayload(payload)).pipe(map((item) => this.normalize(item)));
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  private normalize(item: unknown): Aktivnost {
    const source = (item ?? {}) as Record<string, unknown>;
    const id = Number(source['id'] ?? source['aktivnostId'] ?? source['aktivnost_id']);
    const trajanje = Number(source['rokTrajanja'] ?? source['trajanje']);
    const rawIsActive = source['isActive'] ?? source['isactive'] ?? source['is_activve'];

    const cijena = source['cijena'] != null ? Number(source['cijena']) : null;

    return {
      id: Number.isFinite(id) ? id : undefined,
      naziv: String(source['aktivnost'] ?? '').trim(),
      trajanje: Number.isFinite(trajanje) ? trajanje : 0,
      cijena: cijena != null && Number.isFinite(cijena) ? cijena : null,
      isActive: rawIsActive === true || rawIsActive === 'true' || rawIsActive === 1 || rawIsActive === '1'
    };
  }

  private toApiPayload(payload: Aktivnost): Record<string, unknown> {
    return {
      aktivnost: payload.naziv,
      rokTrajanja: payload.trajanje,
      trajanje: payload.trajanje,
      cijena: payload.cijena,
      isActive: payload.isActive
    };
  }
}
