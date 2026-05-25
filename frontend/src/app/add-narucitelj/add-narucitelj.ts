import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { NaruciteljaService, Narucitelj } from '../services/narucitelj.service';

@Component({
  selector: 'app-add-narucitelj',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './add-narucitelj.html',
  styleUrls: ['./add-narucitelj.css']
})
export class AddNaruciteljComponent {
  naruciteljForm: FormGroup;
  loading = false;
  error: string | null = null;
  success = false;

  constructor(
    private fb: FormBuilder,
    private naruciteljaService: NaruciteljaService,
    private router: Router
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

  onSubmit() {
    if (this.naruciteljForm.valid) {
      this.loading = true;
      this.error = null;
      const newNarucitelj: Narucitelj = this.naruciteljForm.value;
      
      this.naruciteljaService.create(newNarucitelj).subscribe({
        next: (response) => {
          this.success = true;
          this.loading = false;
          console.log('Narucitelj created:', response);
          this.naruciteljForm.reset();

          if (this.isEmbeddedMode()) {
            this.notifyParentModalClose(true);
            return;
          }

          setTimeout(() => this.router.navigate(['/narucitelji']), 700);
        },
        error: (err) => {
          this.error = 'Neuspješno kreiranje naručitelja.';
          this.loading = false;
          console.error('Error creating narucitelj:', err);
        }
      });
    }
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