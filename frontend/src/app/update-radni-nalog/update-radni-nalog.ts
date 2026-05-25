import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PLATFORM_ID, inject } from '@angular/core';
import { RadniNalogService, RadniNalog } from '../services/radni-nalog.service';
import { AktivnostiService, Aktivnost } from '../services/aktivnosti.service';
import { NaruciteljaService, Narucitelj } from '../services/narucitelj.service';

@Component({
  selector: 'app-update-radni-nalog',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './update-radni-nalog.html',
  styleUrls: ['./update-radni-nalog.css'],
})
export class UpdateRadniNalogComponent implements OnInit {
  radniNalogForm: FormGroup;
  loading = false;
  error: string | null = null;
  success = false;
  nalogId?: number;
  private originalNalog?: RadniNalog;
  private pendingAktivnosti: unknown = null;
  private readonly platformId = inject(PLATFORM_ID);
  aktivnostiOptions: Aktivnost[] = [];
  aktivnostiSearchTerm = '';
    get filteredAktivnosti(): Aktivnost[] {
      const term = this.aktivnostiSearchTerm.trim().toLowerCase();
      if (!term) {
        return this.aktivnostiOptions;
      }
      return this.aktivnostiOptions.filter((item) => (item.naziv || '').toLowerCase().includes(term));
    }

    updateAktivnostiSearch(value: string) {
      this.aktivnostiSearchTerm = value;
    }
  narucitelji: Narucitelj[] = [];
  naruciteljSearchTerm = '';

  constructor(
    private fb: FormBuilder,
    private radniNalogService: RadniNalogService,
    private aktivnostiService: AktivnostiService,
    private naruciteljService: NaruciteljaService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
    this.radniNalogForm = this.fb.group({
      brojNaloga: ['RN', Validators.required],
      brojPonude: [''],
      brojRacuna: [''],
      narudzbenica: [''],
      ugovor: [''],
      narucitelj: [''],
      datum: ['', Validators.required],
      objekt: [''],
      opis: [''],
      aktivnosti: [[]],
      fakturirano: [false],
      zavrseno: [false],
      naruciteljId: [null]
    });
  }

  ngOnInit() {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    this.aktivnostiService.getAll().subscribe({
      next: (data) => {
        this.aktivnostiOptions = (data ?? []).filter((item) => item.isActive);
        console.log('AKTIVNOSTI OPTIONS:', this.aktivnostiOptions);
        // If we have pending aktivnosti from nalog, patch them now
        if (this.pendingAktivnosti) {
          this.radniNalogForm.patchValue({
            aktivnosti: this.normalizeAktivnosti(this.pendingAktivnosti)
          });
          this.pendingAktivnosti = null;
        } else if (this.originalNalog?.aktivnosti) {
          this.radniNalogForm.patchValue({
            aktivnosti: this.normalizeAktivnosti(this.originalNalog.aktivnosti)
          });
        }
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load aktivnosti for dropdown', err);
      }
    });

    this.naruciteljService.getAll().subscribe({
      next: (data) => {
        this.narucitelji = data ?? [];
      },
      error: (err) => {
        console.error('Failed to load narucitelji for dropdown', err);
      }
    });

    this.route.params.subscribe(params => {
      this.nalogId = +params['id'];
      if (this.nalogId) {
        this.loadRadniNalog();
      }
    });
  }

  loadRadniNalog() {
    if (!this.nalogId) return;
    this.loading = true;
    this.error = null;
    this.radniNalogService.getById(this.nalogId).subscribe({
      next: (data) => {
        this.originalNalog = data;
        // Ensure brojNaloga always starts with 'RN'
        let brojNaloga = String(data.brojNaloga || '').toUpperCase();
        if (!brojNaloga.startsWith('RN')) {
          brojNaloga = 'RN' + brojNaloga.replace(/^RN/i, '');
        }
        // Always patch all fields except aktivnosti immediately
        this.radniNalogForm.patchValue({
          brojNaloga,
          brojPonude: data.brojPonude || '',
          brojRacuna: data.brojRacuna || '',
          narudzbenica: data.narudzbenica || '',
          ugovor: data.ugovor || '',
          narucitelj: data.narucitelj?.name || data.narucitelj_id || '',
          datum: this.toInputDate(data.datum),
          objekt: data.objekt,
          opis: data.opis ?? data.description ?? '',
          fakturirano: !!data.fakturirano,
          zavrseno: !!data.zavrseno,
          naruciteljId: data.naruciteljId,
          status: data.status
        });
        // Only patch aktivnosti if aktivnostiOptions are loaded
        if (this.aktivnostiOptions && this.aktivnostiOptions.length > 0) {
          this.radniNalogForm.patchValue({
            aktivnosti: this.normalizeAktivnosti(data.aktivnosti)
          });
        } else {
          // Store for later
          this.pendingAktivnosti = data.aktivnosti;
        }
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Neuspjelo učitavanje radnog naloga.';
        this.loading = false;
        console.error('Error loading radni nalog:', err);
      }
    });
  }

  onSubmit() {
    if (this.radniNalogForm.valid && this.nalogId) {
      this.loading = true;
      this.error = null;
      const formValue = this.radniNalogForm.value;
      // Ensure brojNaloga starts with 'RN'
      let brojNaloga = String(formValue.brojNaloga || '').toUpperCase();
      if (!brojNaloga.startsWith('RN')) {
        brojNaloga = 'RN' + brojNaloga.replace(/^RN/i, '');
      }
      const resolvedNaruciteljId =
        Number(formValue.naruciteljId) ||
        this.originalNalog?.naruciteljId ||
        Number((this.originalNalog as any)?.narucitelj_id) ||
        0;

      const { documents: _documents, ...originalWithoutDocuments } = this.originalNalog || {};

      const updated: RadniNalog = {
        ...originalWithoutDocuments,
        brojNaloga,
        brojPonude: formValue.brojPonude,
        brojRacuna: formValue.brojRacuna,
        narudzbenica: formValue.narudzbenica,
        ugovor: formValue.ugovor,
        datum: formValue.datum,
        objekt: formValue.objekt,
        opis: formValue.opis,
        aktivnosti: this.normalizeAktivnosti(formValue.aktivnosti),
        fakturirano: !!formValue.fakturirano,
        zavrseno: !!formValue.zavrseno,
        naruciteljId: resolvedNaruciteljId
      };
      this.radniNalogService.update(this.nalogId, updated).subscribe({
        next: (response) => {
          this.success = true;
          this.loading = false;
          console.log('Radni nalog updated:', response);
          if (typeof window !== 'undefined' && window.parent && window.parent !== window) {
            window.parent.postMessage({ type: 'modal-close', refresh: true }, window.location.origin);
            return;
          }
          setTimeout(() => this.router.navigate(['/radniNalozi']), 500);
        },
        error: (err) => {
          this.error = 'Neuspješno ažuriranje radnog naloga.';
          this.loading = false;
          console.error('Error updating radni nalog:', err);
        }
      });
    }
  }

  private toInputDate(value: string | undefined): string {
    if (!value) {
      return '';
    }

    const parsed = new Date(value);
    if (Number.isNaN(parsed.getTime())) {
      return value;
    }

    return parsed.toISOString().slice(0, 10);
  }

  get selectedAktivnostiLabel(): string {
    const selected = (this.radniNalogForm.get('aktivnosti')?.value as number[] | null) ?? [];
    if (selected.length === 0) {
      return 'Odaberi aktivnosti';
    }

    const labels = selected
      .map((id) => this.aktivnostiOptions.find((option) => option.id === id)?.naziv)
      .filter((naziv): naziv is string => !!naziv);

    return labels.join(', ');
  }

  get selectedNaruciteljLabel(): string {
    const selected = String(this.radniNalogForm.get('narucitelj')?.value ?? '').trim();
    return selected.length > 0 ? selected : 'Odaberi naručitelja';
  }

  get filteredNarucitelji(): Narucitelj[] {
    const term = this.naruciteljSearchTerm.trim().toLowerCase();
    if (!term) {
      return this.narucitelji;
    }

    return this.narucitelji.filter((item) => item.name.toLowerCase().includes(term));
  }

  updateNaruciteljSearch(value: string) {
    this.naruciteljSearchTerm = value;
  }

  selectNarucitelj(narucitelj: Narucitelj, dropdown: HTMLDetailsElement) {
    this.radniNalogForm.patchValue({
      narucitelj: narucitelj.name,
      naruciteljId: narucitelj.id ?? null
    });
    this.naruciteljSearchTerm = '';
    dropdown.removeAttribute('open');
  }

  isAktivnostSelected(id: number): boolean {
    const selected = (this.radniNalogForm.get('aktivnosti')?.value as number[] | null) ?? [];
    return selected.includes(id);
  }

  toggleAktivnost(id: number, checked: boolean) {
    const selected = [...((this.radniNalogForm.get('aktivnosti')?.value as number[] | null) ?? [])];

    if (checked) {
      if (!selected.includes(id)) {
        selected.push(id);
      }
    } else {
      const index = selected.indexOf(id);
      if (index >= 0) {
        selected.splice(index, 1);
      }
    }

    this.radniNalogForm.patchValue({ aktivnosti: selected });
  }

  private normalizeAktivnosti(aktivnosti: unknown): number[] {
    if (!Array.isArray(aktivnosti)) {
      return [];
    }

    const ids = new Set<number>();

    return aktivnosti
      .map((item) => {
        if (typeof item === 'number' && Number.isFinite(item)) {
          return item;
        }

        if (typeof item === 'string') {
          const numericId = Number(item);
          if (Number.isFinite(numericId) && item.trim() !== '') {
            return numericId;
          }

          const option = this.aktivnostiOptions.find((entry) => entry.naziv === item);
          return option?.id;
        }

        if (item && typeof item === 'object') {
          const source = item as Record<string, unknown>;
          const rawId = Number(source['id']);
          if (Number.isFinite(rawId)) {
            return rawId;
          }

          const naziv = String(source['naziv'] ?? source['aktivnost'] ?? '').trim();
          const option = this.aktivnostiOptions.find((entry) => entry.naziv === naziv);
          return option?.id;
        }

        return undefined;
      })
      .filter((item): item is number => Number.isFinite(item))
      .filter((item) => {
        if (ids.has(item)) {
          return false;
        }

        ids.add(item);
        return true;
      });
  }
}