import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface UserRecord {
  id: number;
  username: string;
  role?: string;
  ime?: string;
  prezime?: string;
  email?: string;
  isActive: boolean;
  createdAt: string;
}

export interface PonudaAdmin {
  id: number;
  korisnikId: number;
  opis: string;
  vrstaAtesta?: string;
  lokacija?: string;
  zeljeniDatum?: string;
  status: string;
  odgovor?: string;
  korisnik?: { id: number; ime: string; prezime: string; email: string; tvrtka?: string };
  createdAt: string;
}

export interface RecenzijaAdmin {
  id: number;
  korisnikId: number;
  ocjena: number;
  komentar?: string;
  odgovor?: string;
  korisnik?: { id: number; ime: string; prezime: string; email: string };
  createdAt: string;
}

export interface DashboardStats {
  totalNalozi: number;
  totalNarucitelji: number;
  fakturirano: number;
  zavrseno: number;
  nefakturirano: number;
  uTijeku: number;
}

export interface RevenueItem {
  id: number;
  aktivnost: string;
  cijena: number;
  count: number;
  ukupno: number;
}

export interface PerformanceItem {
  id: number;
  username: string;
  ime?: string;
  prezime?: string;
  total: number;
  zavrseno: number;
  fakturirano: number;
}

export interface MonthlyItem {
  month: string;
  count: number;
}

export interface SustavLogItem {
  id: number;
  action: string;
  entity: string;
  entityId?: number;
  details?: string;
  user?: { id: number; username: string; ime?: string; prezime?: string };
  createdAt: string;
}

export interface NaloziReportItem {
  id: number;
  datum: string;
  status: string;
  naruciteljNaziv: string;
  assignedUser?: string;
  aktivnostiCount: number;
}

export interface NaruciteljiReportItem {
  id: number;
  naziv: string;
  adresa: string;
  kontakt: string;
  naloziCount: number;
}

export interface KorisnikBasic {
  id: number;
  ime: string;
  prezime: string;
  email: string;
  tvrtka?: string;
}

@Injectable({ providedIn: 'root' })
export class ManagementService {
  private readonly http = inject(HttpClient);

  // Users
  getUsers(): Observable<UserRecord[]> {
    return this.http.get<UserRecord[]>(`${environment.apiUrl}/users`);
  }
  getUser(id: number): Observable<UserRecord> {
    return this.http.get<UserRecord>(`${environment.apiUrl}/users/${id}`);
  }
  createUser(data: { username: string; password: string; role: string; ime?: string; prezime?: string; email?: string }): Observable<UserRecord> {
    return this.http.post<UserRecord>(`${environment.apiUrl}/users`, data);
  }
  updateUser(id: number, data: Partial<UserRecord & { password?: string }>): Observable<UserRecord> {
    return this.http.put<UserRecord>(`${environment.apiUrl}/users/${id}`, data);
  }
  deactivateUser(id: number): Observable<UserRecord> {
    return this.http.put<UserRecord>(`${environment.apiUrl}/users/${id}/deactivate`, {});
  }

  // Ponude management
  getAllPonude(): Observable<PonudaAdmin[]> {
    return this.http.get<PonudaAdmin[]>(`${environment.apiUrl}/management/ponude`);
  }
  getPonuda(id: number): Observable<PonudaAdmin> {
    return this.http.get<PonudaAdmin>(`${environment.apiUrl}/management/ponude/${id}`);
  }
  updatePonudaStatus(id: number, status: string, odgovor?: string): Observable<PonudaAdmin> {
    return this.http.put<PonudaAdmin>(`${environment.apiUrl}/management/ponude/${id}/status`, { status, odgovor });
  }

  // Recenzije management
  getAllRecenzije(): Observable<RecenzijaAdmin[]> {
    return this.http.get<RecenzijaAdmin[]>(`${environment.apiUrl}/management/recenzije`);
  }
  respondToRecenzija(id: number, odgovor: string): Observable<RecenzijaAdmin> {
    return this.http.put<RecenzijaAdmin>(`${environment.apiUrl}/management/recenzije/${id}/odgovor`, { odgovor });
  }

  // Obavijesti
  sendObavijest(korisnikId: number, naslov: string, poruka: string): Observable<any> {
    return this.http.post(`${environment.apiUrl}/management/obavijesti`, { korisnikId, naslov, poruka });
  }
  getKorisnici(): Observable<KorisnikBasic[]> {
    return this.http.get<KorisnikBasic[]>(`${environment.apiUrl}/management/korisnici`);
  }

  // Statistics
  getDashboardStats(): Observable<DashboardStats> {
    return this.http.get<DashboardStats>(`${environment.apiUrl}/management/statistics/dashboard`);
  }
  getRevenueByAktivnost(): Observable<RevenueItem[]> {
    return this.http.get<RevenueItem[]>(`${environment.apiUrl}/management/statistics/revenue`);
  }
  getPerformanceByWorker(): Observable<PerformanceItem[]> {
    return this.http.get<PerformanceItem[]>(`${environment.apiUrl}/management/statistics/performance`);
  }
  getIssuedByMonth(): Observable<MonthlyItem[]> {
    return this.http.get<MonthlyItem[]>(`${environment.apiUrl}/management/statistics/monthly`);
  }

  // Logs
  getLogs(params?: { entity?: string; action?: string; from?: string; to?: string }): Observable<SustavLogItem[]> {
    return this.http.get<SustavLogItem[]>(`${environment.apiUrl}/management/logs`, { params: params as any });
  }

  // Reports
  getNaloziReport(params?: any): Observable<NaloziReportItem[]> {
    return this.http.get<NaloziReportItem[]>(`${environment.apiUrl}/management/reports/nalozi`, { params });
  }
  getNaruciteljiReport(): Observable<NaruciteljiReportItem[]> {
    return this.http.get<NaruciteljiReportItem[]>(`${environment.apiUrl}/management/reports/narucitelji`);
  }

  // Assign worker
  assignWorker(nalogId: number, assignedUserId: number | null): Observable<any> {
    return this.http.put(`${environment.apiUrl}/radni-nalozi/${nalogId}/assign`, { assignedUserId });
  }

  // My tasks (worker)
  getMyTasks(): Observable<any[]> {
    return this.http.get<any[]>(`${environment.apiUrl}/management/my-tasks`);
  }
}
