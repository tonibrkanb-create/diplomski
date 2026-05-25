import { Component } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { PLATFORM_ID, inject } from '@angular/core';
import { AktivnostiService, Aktivnost } from '../services/aktivnosti.service';

@Component({
  selector: 'app-add-aktivnost',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './add-aktivnost.html',
  styleUrls: ['./add-aktivnost.css']
})
export class AddAktivnostComponent {
  form: FormGroup;
  loading = false;
  error: string | null = null;
  success = false;
  private readonly platformId = inject(PLATFORM_ID);

  constructor(
    private fb: FormBuilder,
    private aktivnostiService: AktivnostiService,
    private router: Router
  ) {
    this.form = this.fb.group({
      naziv: ['', [Validators.required, Validators.maxLength(100)]],
      trajanje: [0, [Validators.required, Validators.min(0)]],
      cijena: [null],
      isActive: [true]
    });
  }

  onSubmit() {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    if (this.form.invalid || this.loading) {
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

    this.aktivnostiService.create(payload).subscribe({
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
        console.error('Error creating aktivnost:', err);
        this.error = 'Dodavanje aktivnosti nije uspjelo.';
        this.loading = false;
      }
    });
  }
}
