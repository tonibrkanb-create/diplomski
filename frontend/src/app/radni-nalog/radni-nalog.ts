import { Component, DestroyRef, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { RadniNalogService, RadniNalog } from '../services/radni-nalog.service';
import { AktivnostiService } from '../services/aktivnosti.service';
import { ConfirmDeletePopupComponent } from '../confirm-delete-popup/confirm-delete-popup';
import { forkJoin, fromEvent } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { isPlatformBrowser } from '@angular/common';
import { PLATFORM_ID } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-radni-nalog',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, ConfirmDeletePopupComponent],
  templateUrl: './radni-nalog.html',
  styleUrls: ['./radni-nalog.css']
})
export class RadniNalogComponent implements OnInit {
  radniNalozi: RadniNalog[] = [];
  private aktivnostiNazivById = new Map<number, string>();
  loading = false;
  error: string | null = null;
  private readonly destroyRef = inject(DestroyRef);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly sanitizer = inject(DomSanitizer);

  // search/pagination
  selectedIds = new Set<number>();
  searchTerm = '';
  searchField = 'brojNaloga';  // default search field
  sortField: string | null = 'brojNaloga';
  sortDirection: 'asc' | 'desc' = 'desc';
  pageSize = 10;
  currentPage = 1;
  showModal = false;
  modalTitle = '';
  modalUrl: SafeResourceUrl | null = null;
  showDeleteModal = false;
  deleting = false;
  pendingDeleteIds: number[] = [];

  // Available fields for searching
  searchableFields = [
    { label: 'Broj Naloga', value: 'brojNaloga' },
    { label: 'Naručitelj', value: 'narucitelj' },
    { label: 'Datum', value: 'datum' },
    { label: 'Objekt', value: 'objekt' },
    { label: 'Opis', value: 'opis' },
    { label: 'Aktivnosti', value: 'aktivnosti' }
  ];

  constructor(
    private radniNalogService: RadniNalogService,
    private aktivnostiService: AktivnostiService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    console.log('RadniNalogComponent initialized');
    this.route.data.subscribe((data) => {
      console.log('Route data:', data);
      const resolved = (data['radniNalozi'] as RadniNalog[] | undefined) ?? [];
      this.applyRadniNalozi(resolved);
    });

    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    this.loadAktivnostiOptions();

    const navEntries = performance.getEntriesByType('navigation') as PerformanceNavigationTiming[];
    if (navEntries[0]?.type === 'back_forward') {
      this.loadRadniNalozi();
    }

    fromEvent<PageTransitionEvent>(window, 'pageshow')
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((event) => {
        if (window.location.pathname === '/radniNalozi' && event.persisted) {
          this.loadRadniNalozi();
        }
      });

    fromEvent<MessageEvent>(window, 'message')
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((event) => {
        if (event.origin !== window.location.origin) {
          return;
        }

        if (event.data?.type === 'modal-close') {
          this.closeModal(!!event.data?.refresh);
        }
      });
  }

  loadRadniNalozi() {
    this.loading = true;
    this.error = null;

    this.radniNalogService.getAll().subscribe({
      next: (data) => {
        this.applyRadniNalozi(data);
        console.log('Loaded radni nalozi:', data);
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Neuspjelo učitavanje radnih naloga.';
        console.error('Error loading radni nalozi:', err);
        this.loading = false;
      }
    });
  }

  private loadAktivnostiOptions() {
    this.aktivnostiService.getAll().subscribe({
      next: (data) => {
        this.aktivnostiNazivById.clear();
        (data ?? []).forEach((item) => {
          if (item.id && item.naziv) {
            this.aktivnostiNazivById.set(item.id, item.naziv);
          }
        });
      },
      error: (err) => {
        console.error('Error loading aktivnosti options:', err);
      }
    });
  }

  private applyRadniNalozi(data: RadniNalog[]) {
    this.radniNalozi = data.map((nalog) => ({
      ...nalog,
      id: this.resolveRouteId(nalog) ?? nalog.id
    }));

    const validIds = new Set(this.radniNalozi.map((n) => this.resolveRouteId(n)).filter((id): id is number => !!id));
    this.selectedIds.forEach((id) => {
      if (!validIds.has(id)) {
        this.selectedIds.delete(id);
      }
    });

    if (this.radniNalozi.length > 0 && this.filtered.length === 0) {
      this.searchTerm = '';
      this.searchField = 'brojNaloga';
      this.currentPage = 1;
    }

    this.ensureValidPage();
  }

  getRouteId(nalog: RadniNalog): number | null {
    return this.resolveRouteId(nalog);
  }

  navigateToDetails(nalog: RadniNalog) {
    const routeId = this.resolveRouteId(nalog);
    if (!routeId) {
      return;
    }

    this.router.navigate(['/radni-nalog', routeId]);
  }

  editNalog(nalog: RadniNalog) {
    const routeId = this.resolveRouteId(nalog);
    if (!routeId) {
      return;
    }

    this.router.navigate(['/updateRadniNalog', routeId]);
  }

  editSelected() {
    if (this.selectedIds.size !== 1) {
      return;
    }

    const [id] = Array.from(this.selectedIds);
    this.openModal(`/updateRadniNalog/${id}`, 'Uredi radni nalog');
  }

  openAddModal() {
    this.openModal('/addRadniNalog', 'Dodaj radni nalog');
  }

  deleteNalog(nalog: RadniNalog) {
    const routeId = this.resolveRouteId(nalog);
    if (!routeId) {
      return;
    }
    this.openDeleteModal([routeId]);
  }

  deleteSelected() {
    if (this.selectedIds.size === 0) {
      return;
    }

    this.openDeleteModal(Array.from(this.selectedIds));
  }

  get deleteModalMessage(): string {
    return this.pendingDeleteIds.length <= 1
      ? 'Jeste li sigurni da želite obrisati ovaj radni nalog?'
      : `Jeste li sigurni da želite obrisati ${this.pendingDeleteIds.length} odabranih radnih naloga?`;
  }

  closeDeleteModal() {
    if (this.deleting) {
      return;
    }

    this.showDeleteModal = false;
    this.pendingDeleteIds = [];
  }

  confirmDeleteModal() {
    if (this.pendingDeleteIds.length === 0 || this.deleting) {
      return;
    }

    this.deleting = true;
    const ids = [...this.pendingDeleteIds];
    forkJoin(ids.map((id) => this.radniNalogService.delete(id))).subscribe({
      next: () => {
        this.deleting = false;
        this.showDeleteModal = false;
        this.pendingDeleteIds = [];
        ids.forEach((id) => this.selectedIds.delete(id));

        const selectedSet = new Set(ids);
        this.applyRadniNalozi(this.radniNalozi.filter((item) => {
          const routeId = this.resolveRouteId(item);
          return !routeId || !selectedSet.has(routeId);
        }));
      },
      error: (err) => {
        this.deleting = false;
        this.error = 'Neuspješno brisanje radnog naloga.';
        console.error('Error deleting radni nalog:', err);
      }
    });
  }

  private openDeleteModal(ids: number[]) {
    if (ids.length === 0) {
      return;
    }

    this.pendingDeleteIds = [...ids];
    this.error = null;
    this.showDeleteModal = true;
  }

  toggleSelect(id: number | null) {
    if (!id) {
      return;
    }

    if (this.selectedIds.has(id)) {
      this.selectedIds.delete(id);
      return;
    }

    this.selectedIds.add(id);
  }

  toggleSelectAll(checked: boolean) {
    if (checked) {
      this.paged.forEach((nalog) => {
        const id = this.resolveRouteId(nalog);
        if (id) {
          this.selectedIds.add(id);
        }
      });
      return;
    }

    this.paged.forEach((nalog) => {
      const id = this.resolveRouteId(nalog);
      if (id) {
        this.selectedIds.delete(id);
      }
    });
  }

  isAllSelected() {
    return this.paged.length > 0 && this.paged.every((nalog) => {
      const id = this.resolveRouteId(nalog);
      return !!id && this.selectedIds.has(id);
    });
  }

  get selectedCount() {
    return this.selectedIds.size;
  }

  closeModal(refresh = true) {
    this.showModal = false;
    this.modalTitle = '';
    this.modalUrl = null;

    if (refresh) {
      this.loadRadniNalozi();
    }
  }

  private openModal(path: string, title: string) {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    const popupUrl = new URL(path.replace(/^\//, ''), document.baseURI);
    popupUrl.searchParams.set('embedded', '1');

    this.modalTitle = title;
    this.modalUrl = this.sanitizer.bypassSecurityTrustResourceUrl(popupUrl.toString());
    this.showModal = true;
  }

  private resolveRouteId(nalog: RadniNalog): number | null {
    return nalog.id ?? null;
  }

  get filtered() {
    const term = (this.searchTerm || '').trim().toLowerCase();
    const filtered = this.radniNalozi.filter(nalog => {
      if (!term) {
        return true;
      }

      let fieldValue: any;
      if (this.searchField === 'narucitelj') {
        fieldValue = nalog.narucitelj?.name || nalog.narucitelj_id;
      } else if (this.searchField === 'aktivnosti') {
        fieldValue = this.renderAktivnosti(nalog.aktivnosti);
      } else {
        fieldValue = (nalog as any)[this.searchField];
      }
      if (fieldValue === null || fieldValue === undefined) return false;
      return fieldValue.toString().toLowerCase().includes(term);
    });

    if (!this.sortField) {
      return filtered;
    }

    return [...filtered].sort((a, b) => this.compareByField(a, b, this.sortField!));
  }

  toggleSort(field: string) {
    if (this.sortField === field) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortField = field;
      this.sortDirection = 'asc';
    }
    this.currentPage = 1;
  }

  isSortedBy(field: string) {
    return this.sortField === field;
  }

  getSortArrow(field: string) {
    if (this.sortField !== field) {
      return '';
    }
    return this.sortDirection === 'asc' ? '▲' : '▼';
  }

  private compareByField(a: RadniNalog, b: RadniNalog, field: string) {
    const valueA = this.getSortValue(a, field);
    const valueB = this.getSortValue(b, field);

    if (valueA < valueB) {
      return this.sortDirection === 'asc' ? -1 : 1;
    }
    if (valueA > valueB) {
      return this.sortDirection === 'asc' ? 1 : -1;
    }
    return 0;
  }

  private getSortValue(nalog: RadniNalog, field: string) {
    let value: any = null;
    if (field === 'narucitelj') {
      value = nalog.narucitelj?.name || nalog.narucitelj_id;
    } else if (field === 'aktivnosti') {
      value = this.renderAktivnosti(nalog.aktivnosti);
    } else {
      value = (nalog as any)[field];
    }

    if (field === 'brojNaloga') {
      const numeric = Number(value);
      if (!Number.isNaN(numeric)) {
        return numeric;
      }
    }

    if (field === 'datum') {
      const parsed = Date.parse(String(value));
      if (!Number.isNaN(parsed)) {
        return parsed;
      }
    }

    if (typeof value === 'boolean') {
      return value ? 1 : 0;
    }

    return String(value ?? '').toLowerCase();
  }

  get paged() {
    this.ensureValidPage();
    const start = (this.currentPage - 1) * this.pageSize;
    // Format datum to show only date (no time)
    return this.filtered.slice(start, start + this.pageSize).map(nalog => ({
      ...nalog,
      datum: this.formatDateOnly(nalog.datum)
    }));
  }

  private formatDateOnly(value: string): string {
    if (!value) return '-';
    const parsed = new Date(value);
    if (Number.isNaN(parsed.getTime())) return value;
    return parsed.toLocaleDateString('hr-HR');
  }

  get maxPage(): number {
    return Math.max(1, Math.ceil(this.filtered.length / this.pageSize));
  }

  getPageNumbers(): number[] {
    const pages = [];
    for (let i = 1; i <= this.maxPage; i++) {
      pages.push(i);
    }
    return pages;
  }

  changePage(delta: number) {
    const target = this.currentPage + delta;
    const max = this.maxPage;
    this.currentPage = Math.min(Math.max(1, target), max);
  }

  setPage(page: number) {
    const max = this.maxPage;
    this.currentPage = Math.min(Math.max(1, page), max);
  }

  private ensureValidPage() {
    const max = this.maxPage;
    if (!Number.isFinite(this.currentPage) || this.currentPage < 1) {
      this.currentPage = 1;
      return;
    }
    if (this.currentPage > max) {
      this.currentPage = max;
    }
  }

  renderAktivnosti(aktivnosti: Array<string | number> | undefined): string {
    if (!Array.isArray(aktivnosti) || aktivnosti.length === 0) {
      return '';
    }

    return aktivnosti
      .map((item) => {
        if (typeof item === 'number') {
          return this.aktivnostiNazivById.get(item) ?? String(item);
        }

        const normalized = String(item ?? '').trim();
        if (!normalized) {
          return '';
        }

        const numericId = Number(normalized);
        if (Number.isFinite(numericId)) {
          return this.aktivnostiNazivById.get(numericId) ?? normalized;
        }

        return normalized;
      })
      .filter((value) => value.length > 0)
      .join(', ');
  }
}