import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { KorisnikService, Ponuda } from '../services/korisnik.service';

@Component({
  selector: 'app-korisnik-ponude',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './korisnik-ponude.html',
  styleUrl: './korisnik-ponude.css',
})
export class KorisnikPonudeComponent implements OnInit {
  ponude = signal<Ponuda[]>([]);
  showForm = signal(false);
  form: FormGroup;
  errorMessage = signal('');
  successMessage = signal('');

  constructor(
    private readonly fb: FormBuilder,
    private readonly korisnikService: KorisnikService
  ) {
    this.form = this.fb.group({
      opis: ['', Validators.required],
      vrstaAtesta: [''],
      lokacija: [''],
      zeljeniDatum: ['']
    });
  }

  ngOnInit() {
    this.loadPonude();
  }

  loadPonude() {
    this.korisnikService.getPonude().subscribe({
      next: (list) => this.ponude.set(list)
    });
  }

  toggleForm() {
    this.showForm.set(!this.showForm());
    this.errorMessage.set('');
    this.successMessage.set('');
  }

  onSubmit() {
    if (this.form.invalid) {
      this.errorMessage.set('Opis je obavezan');
      return;
    }

    this.errorMessage.set('');
    this.korisnikService.createPonuda(this.form.value).subscribe({
      next: () => {
        this.successMessage.set('Zahtjev uspješno poslan');
        this.form.reset();
        this.showForm.set(false);
        this.loadPonude();
      },
      error: (err) => {
        this.errorMessage.set(err.error?.message || 'Greška pri slanju zahtjeva');
      }
    });
  }

  acceptPonuda(id: number) {
    this.errorMessage.set('');
    this.successMessage.set('');
    this.korisnikService.updatePonudaStatus(id, 'odobrena').subscribe({
      next: () => {
        this.successMessage.set('Ponuda prihvaćena. Radnik je obaviješten.');
        this.loadPonude();
      },
      error: (err) => {
        this.errorMessage.set(err.error?.message || 'Neuspjelo prihvaćanje ponude');
      }
    });
  }

  rejectPonuda(id: number) {
    this.errorMessage.set('');
    this.successMessage.set('');
    this.korisnikService.updatePonudaStatus(id, 'odbijena').subscribe({
      next: () => {
        this.successMessage.set('Ponuda odbijena. Radnik je obaviješten.');
        this.loadPonude();
      },
      error: (err) => {
        this.errorMessage.set(err.error?.message || 'Neuspjelo odbijanje ponude');
      }
    });
  }

  getStatusLabel(status: string): string {
    const labels: Record<string, string> = {
      nova: 'Nova',
      poslana: 'Poslana',
      odobrena: 'Odobrena',
      odbijena: 'Odbijena'
    };
    return labels[status] || status;
  }
}
