import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { KorisnikService, Recenzija } from '../services/korisnik.service';

@Component({
  selector: 'app-korisnik-recenzije',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './korisnik-recenzije.html',
  styleUrl: './korisnik-recenzije.css',
})
export class KorisnikRecenzijeComponent implements OnInit {
  recenzije = signal<Recenzija[]>([]);
  showForm = signal(false);
  form: FormGroup;
  errorMessage = signal('');
  successMessage = signal('');

  constructor(
    private readonly fb: FormBuilder,
    private readonly korisnikService: KorisnikService
  ) {
    this.form = this.fb.group({
      ocjena: [5, [Validators.required, Validators.min(1), Validators.max(5)]],
      komentar: ['']
    });
  }

  ngOnInit() {
    this.loadRecenzije();
  }

  loadRecenzije() {
    this.korisnikService.getRecenzije().subscribe({
      next: (list) => this.recenzije.set(list)
    });
  }

  toggleForm() {
    this.showForm.set(!this.showForm());
    this.errorMessage.set('');
    this.successMessage.set('');
  }

  onSubmit() {
    if (this.form.invalid) {
      this.errorMessage.set('Ocjena je obavezna (1-5)');
      return;
    }

    this.errorMessage.set('');
    this.korisnikService.createRecenzija(this.form.value).subscribe({
      next: () => {
        this.successMessage.set('Recenzija uspješno dodana');
        this.form.reset({ ocjena: 5 });
        this.showForm.set(false);
        this.loadRecenzije();
      },
      error: (err) => {
        this.errorMessage.set(err.error?.message || 'Greška pri dodavanju recenzije');
      }
    });
  }

  getStars(ocjena: number): string {
    return '★'.repeat(ocjena) + '☆'.repeat(5 - ocjena);
  }
}
