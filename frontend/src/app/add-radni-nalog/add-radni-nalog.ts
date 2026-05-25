import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { PLATFORM_ID, inject } from '@angular/core';
import { RadniNalogService, RadniNalog } from '../services/radni-nalog.service';
import { NaruciteljaService, Narucitelj } from '../services/narucitelj.service';
import { AktivnostiService, Aktivnost } from '../services/aktivnosti.service';

@Component({
  selector: 'app-add-radni-nalog',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './add-radni-nalog.html',
  styleUrls: ['./add-radni-nalog.css'],
})
export class AddRadniNalogComponent implements OnInit {
    nextBrojNaloga: string | null = null;
  radniNalogForm: FormGroup;
  loading = false;
  error: string | null = null;
  success = false;
  aktivnostiOptions: Aktivnost[] = [];
  aktivnostiSearchTerm = '';

  narucitelji: Narucitelj[] = [];
  naruciteljSearchTerm = '';
  private readonly platformId = inject(PLATFORM_ID);

  constructor(
    private fb: FormBuilder,
    private radniNalogService: RadniNalogService,
    private naruciteljService: NaruciteljaService,
    private aktivnostiService: AktivnostiService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
    this.radniNalogForm = this.fb.group({
      brojNaloga: [''],
      brojPonude: [''],
      brojRacuna: [''],
      narudzbenica: [''],
      ugovor: [''],
      naruciteljName: ['', Validators.required],
      naruciteljId: ['', Validators.required],
      datum: ['', Validators.required],
      objekt: [''],
      opis: [''],
      aktivnosti: [[]],
      fakturirano: [false],
      zavrseno: [false]
    });

    this.radniNalogForm.get('naruciteljName')?.valueChanges.subscribe((name: string) => {
      const match = this.narucitelji.find((n) => n.name.toLowerCase() === name.toLowerCase());
      if (match) {
        this.radniNalogForm.patchValue({ naruciteljId: match.id });
      } else {
        this.radniNalogForm.patchValue({ naruciteljId: '' });
      }
    });
  }

  ngOnInit() {
        // Fetch next suggested broj naloga from backend
    this.radniNalogService.getNextBrojNaloga().subscribe({
      next: (val: string) => {
        let suggestion = '';
        // Try to parse JSON if present in the string
        const match = val.match(/^RN\s*(\{.*\})$/);
        if (match) {
          try {
            const parsed = JSON.parse(match[1]);
            if (parsed && typeof parsed.brojNaloga === 'string') {
              suggestion = parsed.brojNaloga;
            }
          } catch {
            suggestion = val;
          }
        } else if (typeof val === 'string' && val.toUpperCase().startsWith('RN')) {
          suggestion = val;
        } else if (typeof val === 'string') {
          suggestion = 'RN' + val.replace(/^RN/i, '');
          const match = suggestion.match(/^RN\s*(\{.*\})$/);
          if (match) {
            try {
              const parsed = JSON.parse(match[1]);
              if (parsed && typeof parsed.brojNaloga === 'string') {
                suggestion = parsed.brojNaloga;
              }
            } catch {
              // If parsing fails, keep the original string
            }
          }
        }
        this.nextBrojNaloga = suggestion;
        // Only set if user hasn't typed anything (default or empty)
        const current = this.radniNalogForm.get('brojNaloga')?.value;
        if (!current || current === 'RN' || current === '') {
          this.radniNalogForm.patchValue({ brojNaloga: suggestion });
        }
      },
      error: (_err: unknown) => {
        this.nextBrojNaloga = null;
        // Optionally log error
      }
    });
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    this.naruciteljService.getAll().subscribe({
      next: (data) => {
        this.narucitelji = data;
      },
      error: (err) => {
        console.error('Failed to load narucitelji for dropdown', err);
      }
    });

    this.aktivnostiService.getAll().subscribe({
      next: (data) => {
        this.aktivnostiOptions = (data ?? []).filter((item) => item.isActive);
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load aktivnosti for dropdown', err);
      }
    });
  }

  get selectedNaruciteljLabel(): string {
    const selected = String(this.radniNalogForm.get('naruciteljName')?.value ?? '').trim();
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

  selectNarucitelj(narucitelj: Narucitelj, dropdown: HTMLDetailsElement) {
    this.radniNalogForm.patchValue({
      naruciteljName: narucitelj.name,
      naruciteljId: narucitelj.id ?? ''
    });
    this.naruciteljSearchTerm = '';
    dropdown.removeAttribute('open');
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

  onSubmit() {
    if (this.radniNalogForm.valid) {
      this.loading = true;
      this.error = null;
      const { naruciteljName, ...formValue } = this.radniNalogForm.value;
      // Ensure brojNaloga starts with 'RN'
      let brojNaloga = String(formValue.brojNaloga || '').toUpperCase();
      if (!brojNaloga.startsWith('RN')) {
        brojNaloga = 'RN' + brojNaloga.replace(/^RN/i, '');
      }
      const aktivnostiIds = this.normalizeAktivnostiIds(formValue.aktivnosti);
      const newNalog: RadniNalog & { aktivnostiIds: number[]; aktivnostIds: number[] } = {
        ...formValue,
        brojNaloga,
        aktivnosti: aktivnostiIds
      };
      this.radniNalogService.create(newNalog).subscribe({
        next: (response) => {
          this.success = true;
          this.loading = false;
          console.log('Radni nalog created:', response);
          this.radniNalogForm.reset();

          if (this.isEmbeddedMode()) {
            console.log('Notifying parent about modal close with refresh');
            this.notifyParentModalClose(true);
            return;
          }

          setTimeout(() => this.router.navigate(['/radniNalozi']), 700);
        },
        error: (err) => {
          this.error = 'Neuspješno kreiranje radnog naloga.';
          this.loading = false;
          console.error('Error creating radni nalog:', err);
        }
      });
    }
  }

  private normalizeAktivnostiIds(value: unknown): number[] {
    console.log('Normalizing aktivnosti IDs from value:', value);
    if (!Array.isArray(value)) {
      console.log('Value is not an array, returning empty array');
      return [];
    }

    var result = value
      .map((item) => Number(item))
      .filter((item) => Number.isFinite(item));

      console.log('Normalized aktivnosti IDs:', result);

    return result;
  }

  private isEmbeddedMode(): boolean {
    if (typeof window === 'undefined') {
      return false;
    }

    const params = new URLSearchParams(window.location.search);
    return params.get('embedded') === '1' || window.self !== window.top;
  }

  private notifyParentModalClose(refresh: boolean) {
    if (typeof window === 'undefined' || !window.parent || window.parent === window) {
      return;
    }

    const payload = { type: 'modal-close', refresh };

    try {
      window.parent.postMessage(payload, window.location.origin);
    } catch {
      window.parent.postMessage(payload, '*');
    }
  }
}
