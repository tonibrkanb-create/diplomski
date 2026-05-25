import { Component, OnInit } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PLATFORM_ID, inject } from '@angular/core';
import { Narucitelj, NaruciteljaService } from '../services/narucitelj.service';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-update-narucitelj',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './update-narucitelj.html',
  styleUrl: './update-narucitelj.css'
})
export class UpdateNaruciteljComponent implements OnInit {
  naruciteljForm: FormGroup;
  loading = false;
  saving = false;
  error: string | null = null;
  success = false;
  private naruciteljId: number | null = null;
  private originalNarucitelj?: Narucitelj;
  private readonly platformId = inject(PLATFORM_ID);

  constructor(
    private readonly fb: FormBuilder,
    private readonly naruciteljaService: NaruciteljaService,
    private readonly route: ActivatedRoute,
    private readonly router: Router
  ) {
    this.naruciteljForm = this.fb.group({
      name: ['', Validators.required],
      adresa: ['', Validators.required],
      mjesto: ['', Validators.required],
      postanskiBroj: [''],
      drzava: [''],
      OIB: [''],
      ziroRacun: [''],
      ostalo: [''],
      kontaktOsoba: [''],
      telefon: [''],
      mobitel: [''],
      fax: [''],
      email: ['', Validators.email]
    });
  }

  ngOnInit() {    
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }
      this.route.params.subscribe(params => {
      this.naruciteljId = +params['id'];
      if (this.naruciteljId) {
        this.loadNarucitelj();
      }
    });
  }

  onSubmit() {
    if (this.naruciteljForm.invalid || this.naruciteljId === null) {
      this.naruciteljForm.markAllAsTouched();
      return;
    }

    this.saving = true;
    this.error = null;

    const formValue = this.naruciteljForm.value;
    const updatedNarucitelj: Narucitelj = {
      ...(this.originalNarucitelj || {}),
      name: formValue.name,
      narucitelj: formValue.name,
      adresa: formValue.adresa,
      mjesto: formValue.mjesto,
      postanskiBroj: formValue.postanskiBroj,
      drzava: formValue.drzava,
      OIB: formValue.OIB,
      oib: formValue.OIB,
      ziroRacun: formValue.ziroRacun,
      ostalo: formValue.ostalo,
      kontaktOsoba: formValue.kontaktOsoba,
      telefon: formValue.telefon,
      mobitel: formValue.mobitel,
      fax: formValue.fax,
      email: formValue.email
    };

    this.naruciteljaService.update(this.naruciteljId, updatedNarucitelj).subscribe({
      next: () => {
        this.success = true;
        this.saving = false;
        if (typeof window !== 'undefined' && window.parent && window.parent !== window) {
          window.parent.postMessage({ type: 'modal-close', refresh: true }, window.location.origin);
          return;
        }
        setTimeout(() => this.router.navigate(['/narucitelji']), 700);
      },
      error: (err) => {
        this.error = 'Neuspješno ažuriranje naručitelja.';
        this.saving = false;
        console.error('Error updating narucitelj:', err);
      }
    });
  }

  cancel() {
    if (typeof window !== 'undefined' && window.parent && window.parent !== window) {
      window.parent.postMessage({ type: 'modal-close', refresh: false }, window.location.origin);
      return;
    }

    this.router.navigate(['/narucitelji']);
  }

  private loadNarucitelj() {
    if (!this.naruciteljId) {
      this.error = 'Neispravan ID naručitelja.';
      return;
    }

    this.loading = true;
    this.error = null;

    this.naruciteljaService.getById(this.naruciteljId)
      .pipe(finalize(() => { this.loading = false; }))
      .subscribe({
      next: (narucitelj) => {
        try {
          const value = (narucitelj ?? {}) as Partial<Narucitelj>;
          this.originalNarucitelj = value as Narucitelj;

          this.naruciteljForm.patchValue({
            name: value.name ?? '',
            adresa: value.adresa ?? '',
            mjesto: value.mjesto ?? '',
            postanskiBroj: value.postanskiBroj ?? '',
            drzava: value.drzava ?? '',
            OIB: value.OIB ?? '',
            ziroRacun: value.ziroRacun ?? '',
            ostalo: value.ostalo ?? '',
            kontaktOsoba: value.kontaktOsoba ?? '',
            telefon: value.telefon ?? '',
            mobitel: value.mobitel ?? '',
            fax: value.fax ?? '',
            email: value.email ?? ''
          });
        } catch (mapErr) {
          this.error = 'Neuspješno čitanje odgovora za naručitelja.';
          console.error('Error parsing narucitelj response:', mapErr, narucitelj);
        }
      },
      error: (err) => {
        this.error = 'Neuspjelo učitavanje naručitelja.';
        console.error('Error loading narucitelj:', err);
      }
    });
  }
}