import { Component, DestroyRef, OnInit, inject } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { NaruciteljaService, Narucitelj } from '../services/narucitelj.service';
import { ConfirmDeletePopupComponent } from '../confirm-delete-popup/confirm-delete-popup';
import { PLATFORM_ID } from '@angular/core';
import { forkJoin, fromEvent } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-narucitelji',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, ConfirmDeletePopupComponent],
  templateUrl: './narucitelj.html',
  styleUrls: ['./narucitelj.css']
})
export class NaruciteljiComponent implements OnInit {
  narucitelji: Narucitelj[] = [];
  loading = false;
  error: string | null = null;
  private readonly destroyRef = inject(DestroyRef);
  private readonly platformId = inject(PLATFORM_ID);

  // selection & search/pagination
  selectedIds = new Set<number>();
  searchTerm = '';
  searchField = 'name';  // default search field
  sortField: string | null = 'id';
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
    { label: 'Naručitelj', value: 'name' },
    { label: 'Adresa', value: 'adresa' },
    { label: 'Mjesto', value: 'mjesto' },
    { label: 'Email', value: 'email' },
    { label: 'Telefon', value: 'telefon' },
    { label: 'Mobitel', value: 'mobitel' },
    { label: 'OIB', value: 'OIB' },
    { label: 'Kontakt osoba', value: 'kontaktOsoba' }
  ];

  constructor(
    private naruciteljaService: NaruciteljaService,
    private route: ActivatedRoute,
    private router: Router,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit() {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    this.route.data.subscribe((data) => {
      const resolved = (data['narucitelji'] as Narucitelj[] | undefined) ?? [];
      this.applyNarucitelji(resolved);
    });

    this.loadNarucitelji();

    const navEntries = performance.getEntriesByType('navigation') as PerformanceNavigationTiming[];
    if (navEntries[0]?.type === 'back_forward') {
      this.loadNarucitelji();
    }

    fromEvent<PageTransitionEvent>(window, 'pageshow')
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((event) => {
        if (window.location.pathname === '/narucitelji' && event.persisted) {
          this.loadNarucitelji();
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

  loadNarucitelji() {
    this.loading = true;
    this.error = null;
    this.naruciteljaService.getAll().subscribe({
      next: (data) => {
        this.applyNarucitelji(data);
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Neuspjelo učitavanje naručitelja.';
        console.error('Error loading narucitelji:', err);
        this.loading = false;
      }
    });
  }

  private applyNarucitelji(data: Narucitelj[]) {
    this.narucitelji = data;

    const validIds = new Set(this.narucitelji.map((n) => n.id).filter((id): id is number => !!id));
    this.selectedIds.forEach((id) => {
      if (!validIds.has(id)) {
        this.selectedIds.delete(id);
      }
    });

    if (this.narucitelji.length > 0 && this.filtered.length === 0) {
      this.searchTerm = '';
      this.searchField = 'name';
      this.currentPage = 1;
    }

    this.ensureValidPage();
  }

  get filtered() {
    const term = (this.searchTerm || '').trim().toLowerCase();
    const filtered = this.narucitelji.filter(n => {
      if (!term) {
        return true;
      }

      const fieldValue = (n as any)[this.searchField];
      if (fieldValue === null || fieldValue === undefined) return false;
      return fieldValue.toString().toLowerCase().includes(term);
    });

    if (!this.sortField) {
      return filtered;
    }

    return [...filtered].sort((a, b) => this.compareByField(a, b, this.sortField!));
  }

  get paged() {
    this.ensureValidPage();
    const start = (this.currentPage - 1) * this.pageSize;
    return this.filtered.slice(start, start + this.pageSize);
  }

  navigateToDetails(narucitelj: Narucitelj) {
    if (!narucitelj.id) {
      return;
    }

    this.router.navigate(['/narucitelj', narucitelj.id]);
  }

  toggleSelect(id: number | undefined) {
    if (!id) return;
    if (this.selectedIds.has(id)) {
      this.selectedIds.delete(id);
    } else {
      this.selectedIds.add(id);
    }
  }

  toggleSelectAll(checked: boolean) {
    if (checked) {
      this.paged.forEach(n => {
        if (n.id) this.selectedIds.add(n.id);
      });
      return;
    }

    this.paged.forEach(n => {
      if (n.id) this.selectedIds.delete(n.id);
    });
  }

  isAllSelected() {
    return this.paged.length > 0 && this.paged.every(n => n.id && this.selectedIds.has(n.id));
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

  private compareByField(a: Narucitelj, b: Narucitelj, field: string) {
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

  private getSortValue(narucitelj: Narucitelj, field: string) {
    const value = (narucitelj as any)[field];

    if (typeof value === 'number') {
      return value;
    }

    if (typeof value === 'boolean') {
      return value ? 1 : 0;
    }

    return String(value ?? '').toLowerCase();
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

  editNarucitelj(id: number | undefined) {
    if (!id) {
      return;
    }

    this.openModal(`/updateNarucitelj/${id}`, 'Uredi Naručitelja');
  }

  editSelected() {
    if (this.selectedIds.size !== 1) {
      return;
    }

    const [id] = Array.from(this.selectedIds);
    this.openModal(`/updateNarucitelj/${id}`, 'Uredi Naručitelja');
  }

  openAddModal() {
    this.openModal('/addNarucitelj', 'Dodaj Naručitelja');
  }

  deleteNarucitelj(id: number | undefined) {
    if (!id) {
      return;
    }

    this.openDeleteModal([id]);
  }

  deleteSelected() {
    if (this.selectedIds.size === 0) {
      return;
    }

    this.openDeleteModal(Array.from(this.selectedIds));
  }

  get deleteModalMessage(): string {
    return this.pendingDeleteIds.length <= 1
      ? 'Jeste li sigurni da želite obrisati ovog naručitelja?'
      : `Jeste li sigurni da želite obrisati ${this.pendingDeleteIds.length} odabranih naručitelja?`;
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
    forkJoin(ids.map((id) => this.naruciteljaService.delete(id))).subscribe({
      next: () => {
        this.deleting = false;
        this.showDeleteModal = false;
        this.pendingDeleteIds = [];
        ids.forEach((id) => this.selectedIds.delete(id));

        const selectedSet = new Set(ids);
        this.applyNarucitelji(this.narucitelji.filter((item) => !item.id || !selectedSet.has(item.id)));
      },
      error: (err) => {
        this.deleting = false;
        this.error = 'Neuspješno brisanje naručitelja.';
        console.error('Error deleting selected narucitelji:', err);
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

  get selectedCount() {
    return this.selectedIds.size;
  }

  closeModal(refresh = true) {
    this.showModal = false;
    this.modalTitle = '';
    this.modalUrl = null;

    if (refresh) {
      this.loadNarucitelji();
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

}