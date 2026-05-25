import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface PaginatedResponse<T> {
  items: T[];
  totalItems?: number;
  page?: number;
  pageSize?: number;
  totalPages?: number;
}

export interface Narucitelj {
  id?: number;
  name: string;            // server returns 'name' not 'narucitelj'
  narucitelj?: string;     // alias for compatibility
  adresa: string;
  mjesto: string;
  postanskiBroj: string;
  drzava: string;
  OIB: string;             // server returns uppercase 'OIB'
  oib?: string;            // alias for compatibility
  ziroRacun: string;
  ostalo: string;
  kontaktOsoba: string;
  telefon: string;
  mobitel: string;
  fax: string;
  email: string;
  location?: string;       // extra field from server
  comment?: string;        // extra field from server
  createdAt?: Date;
  updatedAt?: Date;
  radniNalozi?: any[];     // relationship to radni nalozi
}

@Injectable({
  providedIn: 'root'
})
export class NaruciteljaService {
  private apiUrl = `${environment.apiUrl}/narucitelji`;

  constructor(private http: HttpClient) { }

  getAll(pageSize?: number, page?: number): Observable<Narucitelj[]> {
    const params = new URLSearchParams();
    if (pageSize) {
      params.set('pageSize', String(pageSize));
    }

    if (page) {
      params.set('page', String(page));
    }

    const query = params.toString();
    return this.http
      .get<Narucitelj[] | PaginatedResponse<Narucitelj>>(`${this.apiUrl}${query ? `?${query}` : ''}`)
      .pipe(map((response) => Array.isArray(response) ? response : response.items));
  }

  getAllWithMeta(pageSize?: number, page?: number): Observable<PaginatedResponse<Narucitelj>> {
    const params = new URLSearchParams();
    if (pageSize) {
      params.set('pageSize', String(pageSize));
    }

    if (page) {
      params.set('page', String(page));
    }

    const query = params.toString();
    return this.http
      .get<Narucitelj[] | PaginatedResponse<Narucitelj>>(`${this.apiUrl}${query ? `?${query}` : ''}`)
      .pipe(
        map((response) => {
          if (Array.isArray(response)) {
            return {
              items: response,
              totalItems: response.length
            };
          }

          return response;
        })
      );
  }

  getById(id: number): Observable<Narucitelj> {
    return this.http.get<Narucitelj>(`${this.apiUrl}/${id}`);
  }

  create(narucitelj: Narucitelj): Observable<Narucitelj> {
    return this.http.post<Narucitelj>(this.apiUrl, narucitelj);
  }

  update(id: number, narucitelj: Narucitelj): Observable<Narucitelj> {
    return this.http.put<Narucitelj>(`${this.apiUrl}/${id}`, narucitelj);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
