import { Component, OnInit } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PLATFORM_ID, inject } from '@angular/core';
import { AktivnostiService, Aktivnost } from '../services/aktivnosti.service';

@Component({
  selector: 'app-update-aktivnost',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './update-aktivnost.html',
  styleUrls: ['./update-aktivnost.css']
})
export class UpdateAktivnostComponent implements OnInit {
  form: FormGroup;
  loading = false;
  error: string | null = null;
  success = false;
  aktivnostId?: number;
  private readonly platformId = inject(PLATFORM_ID);

  constructor(
    private fb: FormBuilder,
    private aktivnostiService: AktivnostiService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.form = this.fb.group({
      naziv: ['', [Validators.required, Validators.maxLength(100)]],
      trajanje: [0, [Validators.required, Validators.min(0)]],
      cijena: [null],
      isActive: [true]
    });
  }

  ngOnInit() {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    this.route.params.subscribe((params) => {
      this.aktivnostId = Number(params['id']);
      if (this.aktivnostId) {
        this.loadAktivnost();
      }
    });
  }

  private loadAktivnost() {
    if (!this.aktivnostId) {
      return;
    }

    this.loading = true;
    this.error = null;

    this.aktivnostiService.getById(this.aktivnostId).subscribe({
      next: (item) => {
        this.form.patchValue({
          naziv: item.naziv,
          trajanje: item.trajanje,
          cijena: item.cijena,
          isActive: item.isActive
        });
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading aktivnost:', err);
        this.error = 'Učitavanje aktivnosti nije uspjelo.';
        this.loading = false;
      }
    });
  }

  onSubmit() {
    if (this.form.invalid || this.loading || !this.aktivnostId) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.error = null;

    const payload: Aktivnost = {
      naziv: (this.form.value.naziv ?? '').trim(),
      trajanje: Number(this.form.value.trajanje ?? 0),
      cijena: this.form.value.cijena != null ? Number(this.form.value.cijena) : null,
      isActive: !!this.form.value.isActive
    };

    this.aktivnostiService.update(this.aktivnostId, payload).subscribe({
      next: () => {
        this.success = true;
        this.loading = false;

        if (typeof window !== 'undefined' && window.parent && window.parent !== window) {
          window.parent.postMessage({ type: 'modal-close', refresh: true }, window.location.origin);
          return;
        }

        setTimeout(() => this.router.navigate(['/postavke']), 400);
      },
      error: (err) => {
        console.error('Error updating aktivnost:', err);
        this.error = 'Spremanje aktivnosti nije uspjelo.';
        this.loading = false;
      }
    });
  }
}
