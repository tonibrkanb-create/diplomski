import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { KorisnikService, Korisnik } from '../services/korisnik.service';

@Component({
  selector: 'app-korisnik-profil',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './korisnik-profil.html',
  styleUrl: './korisnik-profil.css',
})
export class KorisnikProfilComponent implements OnInit {
  form: FormGroup;
  errorMessage = signal('');
  successMessage = signal('');
  loading = signal(true);

  constructor(
    private readonly fb: FormBuilder,
    private readonly korisnikService: KorisnikService,
    private readonly router: Router
  ) {
    this.form = this.fb.group({
      ime: ['', Validators.required],
      prezime: ['', Validators.required],
      email: [{ value: '', disabled: true }],
      telefon: [''],
      tvrtka: [''],
      adresa: [''],
      mjesto: [''],
      postanskiBroj: [''],
      drzava: ['']
    });
  }

  ngOnInit() {
    this.korisnikService.getProfile().subscribe({
      next: (k) => {
        this.form.patchValue(k);
        this.loading.set(false);
      },
      error: () => this.router.navigate(['/korisnik/prijava'])
    });
  }

  onSubmit() {
    if (this.form.invalid) {
      this.errorMessage.set('Ispunite obavezna polja');
      return;
    }

    this.errorMessage.set('');
    this.successMessage.set('');

    this.korisnikService.updateProfile(this.form.getRawValue()).subscribe({
      next: () => {
        this.successMessage.set('Profil uspješno ažuriran');
      },
      error: (err) => {
        this.errorMessage.set(err.error?.message || 'Greška pri ažuriranju profila');
      }
    });
  }
}
