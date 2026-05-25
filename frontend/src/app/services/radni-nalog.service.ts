  /**
   * Gets the next suggested broj naloga from the backend.
   */
import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { HttpParams } from '@angular/common/http';
import { catchError, map, Observable } from 'rxjs';
import { environment } from '../../environments/environment';

import { Narucitelj } from './narucitelj.service';

export interface Document {
  id?: number;
  name: string;
  url?: string;
  createdAt?: Date;
  updatedAt?: Date;
  radni_nalog_id?: number;
}

export interface RadniNalog {
  id?: number;
  naruciteljId: number;
  narucitelj_id?: number;        // server may return this too
  narucitelj?: Narucitelj;       // nested narucitelj object from server
  brojNaloga: string;
  brojPonude?: string;
  brojRacuna?: string;
  narudzbenica?: string;
  ugovor?: string;
  datum: string;
  objekt: string;
  opis?: string;
  aktivnosti?: Array<string | number>;
  fakturirano: boolean;
  zavrseno: boolean;
  pdfUrl?: string;               // PDF URL from server
  documents?: Document[];        // array of documents
  status?: string;               // legacy field
  description?: string;          // legacy field
  createdAt?: Date;
  updatedAt?: Date;
  assignedUser?: any;            // User assigned to this nalog
}

export interface UskoroIsticeItem {
  id?: number;
  narucitelj?: string;
  radniNalog?: string;
  aktivnost?: string;
  datumIsteka?: string;
  isActive?: boolean;
}

export interface UploadDocumentPayload {
  name: string;
  blob?: string;
  url?: string;
}

export interface DownloadDocumentResponse {
  id: number;
  name: string;
  blob: string;
  createdAt?: string;
  updatedAt?: string;
  radni_nalog_id?: number;
}

export interface RadniNalogNote {
  id?: number;
  date: Date;
  text: string;
  radni_nalog_id: number;
}

@Injectable({
  providedIn: 'root'
})
export class RadniNalogService {
    /**
     * Gets the next suggested broj naloga from the backend.
     */
    getNextBrojNaloga(): Observable<string> {
      return this.http.get(`${environment.apiUrl}/radni-nalozi/nextBrojNaloga`, { responseType: 'text' });
    }
  private apiUrl = `${environment.apiUrl}/radni-nalozi`;

  constructor(private http: HttpClient) { }

  getAll(): Observable<RadniNalog[]> {
    return this.http.get<RadniNalog[]>(this.apiUrl);
  }

  getById(id: number): Observable<RadniNalog> {
    return this.http.get<RadniNalog>(`${this.apiUrl}/${id}`);
  }

  create(radniNalog: RadniNalog): Observable<RadniNalog> {
    return this.http.post<RadniNalog>(this.apiUrl, radniNalog);
  }

  update(id: number, radniNalog: RadniNalog): Observable<RadniNalog> {
    return this.http.put<RadniNalog>(`${this.apiUrl}/${id}`, radniNalog);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getByNarucitelj(naruciteljId: number): Observable<RadniNalog[]> {
    return this.http.get<RadniNalog[]>(`${environment.apiUrl}/narucitelji/${naruciteljId}/radni-nalozi`);
  }

  getPdf(id: number): Observable<HttpResponse<Blob>> {
    return this.http.get(`${this.apiUrl}/${id}/downloadPdf`, {
      responseType: 'blob',
      observe: 'response'
    }).pipe(
      catchError(() =>
        this.http.get(`${this.apiUrl}/${id}/pdf`, {
          responseType: 'blob',
          observe: 'response'
        })
      )
    );
  }

  uploadDocument(id: number, payload: UploadDocumentPayload): Observable<RadniNalog | Document> {
    return this.http.post<RadniNalog | Document>(`${this.apiUrl}/${id}/documents`, payload);
  }

  downloadDocumentById(radniNalogId: number, documentId: number): Observable<DownloadDocumentResponse> {
    return this.http.get<DownloadDocumentResponse>(`${this.apiUrl}/${radniNalogId}/documents/${documentId}`);
  }

  deleteDocumentById(radniNalogId: number, documentId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${radniNalogId}/documents/${documentId}`);
  }

  getNotesByRadniNalog(radniNalogId: number): Observable<RadniNalogNote[]> {
    return this.http
      .get<Array<{ id?: number; date?: string; text: string }>>(`${this.apiUrl}/${radniNalogId}/notes`)
      .pipe(
        map((notes) =>
          (notes ?? []).map((data) => ({
            id: data.id,
            date: data.date ? new Date(data.date) : new Date(),
            text: data.text,
            radni_nalog_id: radniNalogId
          }))
        )
      );
  }

  addNote(radniNalogId: number, text: string): Observable<RadniNalogNote> {
    return this.http
      .post<{ id?: number; date?: string; text: string }>(`${this.apiUrl}/${radniNalogId}/notes`, { text })
      .pipe(
        map((data) => ({
          id: data.id,
          date: data.date ? new Date(data.date) : new Date(),
          text: data.text,
          radni_nalog_id: radniNalogId
        }))
      );
  }

  /**
   * Returns items that are expiring soon, as determined by the backend.
   * Each item contains narucitelj, radni nalog, aktivnost and datum isteka.
   */
  getUskoroIstice(days?: number): Observable<UskoroIsticeItem[] | UskoroIsticeItem> {
    let params = new HttpParams();
    if (typeof days === 'number' && Number.isFinite(days)) {
      params = params.set('days', String(30));
    }

    return this.http.get<UskoroIsticeItem[] | UskoroIsticeItem>(`${this.apiUrl}/uskoroIstice`, { params });
  }
}
