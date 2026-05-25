import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { KorisnikService } from '../services/korisnik.service';

@Component({
  selector: 'app-korisnik-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './korisnik-register.html',
  styleUrl: './korisnik-register.css',
})
export class KorisnikRegisterComponent {
  form: FormGroup;
  errorMessage = signal('');
  successMessage = signal('');

  constructor(
    private readonly fb: FormBuilder,
    private readonly korisnikService: KorisnikService,
    private readonly router: Router
  ) {
    this.form = this.fb.group({
      ime: ['', Validators.required],
      prezime: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      telefon: [''],
      tvrtka: ['']
    });
  }

  onSubmit() {
    if (this.form.invalid) {
      this.errorMessage.set('Ispunite sva obavezna polja ispravno');
      return;
    }

    this.errorMessage.set('');
    this.korisnikService.register(this.form.value).subscribe({
      next: () => {
        this.router.navigate(['/korisnik/dashboard']);
      },
      error: (err) => {
        this.errorMessage.set(err.error?.message || 'Registracija nije uspjela');
      }
    });
  }
}
