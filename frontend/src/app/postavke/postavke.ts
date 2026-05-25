import { ChangeDetectorRef, Component, DestroyRef, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { isPlatformBrowser } from '@angular/common';
import { PLATFORM_ID } from '@angular/core';
import { fromEvent } from 'rxjs';
import { forkJoin } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AktivnostiService, Aktivnost } from '../services/aktivnosti.service';
import { RegisterUserFormComponent } from './register-user-form';
import { ConfirmDeletePopupComponent } from '../confirm-delete-popup/confirm-delete-popup';

@Component({
  selector: 'app-postavke',
  standalone: true,
  imports: [CommonModule, ConfirmDeletePopupComponent, RegisterUserFormComponent],
  templateUrl: './postavke.html',
  styleUrls: ['./postavke.css']
})
export class PostavkeComponent implements OnInit {
  onUserRegistered() {
    // Optionally reload users or show a toast
  }
  aktivnosti: Aktivnost[] = [];
  loading = false;
  error: string | null = null;

  activeTab: 'aktivnosti' | 'register' = 'aktivnosti';
  selectedIds = new Set<number>();
  showModal = false;
  modalTitle = '';
  modalUrl: SafeResourceUrl | null = null;
  showDeleteModal = false;
  deleting = false;
  pendingDeleteIds: number[] = [];

  private readonly destroyRef = inject(DestroyRef);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly sanitizer = inject(DomSanitizer);
  private readonly cdr = inject(ChangeDetectorRef);

  constructor(private aktivnostiService: AktivnostiService) {}

  ngOnInit() {
    this.loadAktivnosti();

    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    const navEntries = performance.getEntriesByType('navigation') as PerformanceNavigationTiming[];
    if (navEntries[0]?.type === 'back_forward') {
      this.loadAktivnosti();
    }

    fromEvent<PageTransitionEvent>(window, 'pageshow')
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((event) => {
        if (window.location.pathname === '/postavke' && event.persisted) {
          this.loadAktivnosti();
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

  loadAktivnosti() {
    this.loading = true;
    this.error = null;
    this.cdr.detectChanges();

    this.aktivnostiService.getAll().subscribe({
      next: (items) => {
        this.aktivnosti = items ?? [];
        this.loading = false;

        const validIds = new Set(this.aktivnosti.map((item) => item.id).filter((id): id is number => !!id));
        this.selectedIds.forEach((id) => {
          if (!validIds.has(id)) {
            this.selectedIds.delete(id);
          }
        });

        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error loading aktivnosti:', err);
        this.error = 'Neuspjelo učitavanje aktivnosti.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  setTab(tab: 'aktivnosti' | 'register') {
    this.activeTab = tab;

    if (tab === 'aktivnosti') {
      this.loadAktivnosti();
    }
  }

  openAddModal() {
    this.openModal('/addAktivnost', 'Dodaj Aktivnost');
  }

  editSelected() {
    if (this.selectedIds.size !== 1) {
      return;
    }

    const [id] = Array.from(this.selectedIds);
    this.openModal(`/updateAktivnost/${id}`, 'Uredi Aktivnost');
  }

  deleteSelected() {
    if (this.selectedIds.size === 0) {
      return;
    }

    this.openDeleteModal(Array.from(this.selectedIds));
  }

  get deleteModalMessage(): string {
    return this.pendingDeleteIds.length <= 1
      ? 'Obrisati označenu aktivnost?'
      : `Obrisati ${this.pendingDeleteIds.length} označenih aktivnosti?`;
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
    forkJoin(ids.map((id) => this.aktivnostiService.delete(id))).subscribe({
      next: () => {
        this.deleting = false;
        this.showDeleteModal = false;
        this.pendingDeleteIds = [];
        this.selectedIds.clear();
        this.loadAktivnosti();
      },
      error: (err) => {
        this.deleting = false;
        console.error('Error deleting aktivnost:', err);
        this.error = 'Brisanje jedne ili više aktivnosti nije uspjelo.';
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

  toggleSelect(id: number | undefined) {
    if (!id) {
      return;
    }

    if (this.selectedIds.has(id)) {
      this.selectedIds.delete(id);
    } else {
      this.selectedIds.add(id);
    }
  }

  toggleSelectAll(checked: boolean) {
    if (checked) {
      this.aktivnosti.forEach((item) => {
        if (item.id) {
          this.selectedIds.add(item.id);
        }
      });
      return;
    }

    this.selectedIds.clear();
  }

  isAllSelected() {
    if (this.aktivnosti.length === 0) {
      return false;
    }

    return this.aktivnosti.every((item) => !!item.id && this.selectedIds.has(item.id));
  }

  get selectedCount() {
    return this.selectedIds.size;
  }

  closeModal(refresh = true) {
    this.showModal = false;
    this.modalTitle = '';
    this.modalUrl = null;

    if (refresh) {
      this.loadAktivnosti();
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
}
