import { ChangeDetectorRef, Component, DestroyRef, OnInit, PLATFORM_ID, inject } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { ActivatedRoute, RouterModule, Router } from '@angular/router';
import { NaruciteljaService, Narucitelj } from '../services/narucitelj.service';
import { RadniNalogService, RadniNalog } from '../services/radni-nalog.service';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { fromEvent } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-narucitelj-details',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './narucitelj-details.html',
  styleUrls: ['./narucitelj-details.css']
})
export class NaruciteljDetailsComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private naruciteljaService = inject(NaruciteljaService);
  private radniNalogService = inject(RadniNalogService);
  private cdr = inject(ChangeDetectorRef);
  private readonly destroyRef = inject(DestroyRef);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly sanitizer = inject(DomSanitizer);

  id!: number;
  narucitelj?: Narucitelj;
  history: RadniNalog[] = [];
  loading = false;
  error: string | null = null;
  showUpdateModal = false;
  updateModalUrl: SafeResourceUrl | null = null;

  ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      fromEvent<MessageEvent>(window, 'message')
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe((event) => {
          if (event.origin !== window.location.origin) {
            return;
          }

          if (event.data?.type === 'modal-close') {
            this.closeUpdateModal(!!event.data?.refresh);
          }
        });
    }

    this.route.paramMap.subscribe((params) => {
      const id = Number(params.get('id'));
      console.log('[narucitelj-details] route id:', id);

      if (!Number.isFinite(id) || id <= 0) {
        this.id = 0;
        this.narucitelj = undefined;
        this.history = [];
        this.loading = false;
        this.error = 'Neispravan ID naručitelja.';
        this.cdr.detectChanges();
        return;
      }

      this.loadNarucitelj(id);
      this.loadNaruciteljHistory(id);
    });
  }

  private loadNarucitelj(id: number) {
    this.id = id;
    this.loading = true;
    this.error = null;
    this.cdr.detectChanges();
    console.log('[narucitelj-details] loading started for id:', id);

    this.naruciteljaService.getById(id).subscribe({
      next: (data) => {
        console.log('[narucitelj-details] data arrived for id:', id, data);
        this.narucitelj = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.narucitelj = undefined;
        this.error = `Neuspjelo učitavanje naručitelja: ${err?.message || 'Nepoznata greška'}`;
        this.loading = false;
        this.cdr.detectChanges();
        console.error('Error loading narucitelj:', err);
      }
    });
  }

  private loadNaruciteljHistory(id: number) {
    this.radniNalogService.getByNarucitelj(id).subscribe({
      next: (data) => {
        this.history = data || [];
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.history = [];
        this.cdr.detectChanges();
        console.error('Error loading radni nalozi:', err);
      }
    });
  }

  navigateToDetails(id: number | undefined) {
    if (id) {
      this.router.navigate(['/radni-nalog', id]);
    }
  }

  openUpdateModal() {
    const naruciteljId = this.narucitelj?.id;
    if (!naruciteljId || !isPlatformBrowser(this.platformId)) {
      return;
    }

    const popupUrl = new URL(`updateNarucitelj/${naruciteljId}`, document.baseURI);
    popupUrl.searchParams.set('embedded', '1');

    this.updateModalUrl = this.sanitizer.bypassSecurityTrustResourceUrl(popupUrl.toString());
    this.showUpdateModal = true;
  }

  closeUpdateModal(refresh = true) {
    this.showUpdateModal = false;
    this.updateModalUrl = null;

    if (refresh && this.id) {
      this.loadNarucitelj(this.id);
      this.loadNaruciteljHistory(this.id);
    }
  }
}
