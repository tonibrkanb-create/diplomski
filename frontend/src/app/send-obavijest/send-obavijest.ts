import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ManagementService, KorisnikBasic } from '../services/management.service';

@Component({
  selector: 'app-send-obavijest',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './send-obavijest.html',
  styleUrl: './send-obavijest.css',
})
export class SendObavijestComponent implements OnInit {
  korisnici = signal<KorisnikBasic[]>([]);
  selectedKorisnikId: number | null = null;
  naslov = '';
  poruka = '';
  successMessage = signal('');
  errorMessage = signal('');

  constructor(private readonly mgmt: ManagementService) {}

  ngOnInit() {
    this.mgmt.getKorisnici().subscribe({ next: (k) => this.korisnici.set(k) });
  }

  send() {
    if (!this.selectedKorisnikId || !this.naslov.trim() || !this.poruka.trim()) {
      this.errorMessage.set('Sva polja su obavezna.');
      return;
    }
    this.errorMessage.set('');
    this.mgmt.sendObavijest(this.selectedKorisnikId, this.naslov, this.poruka).subscribe({
      next: () => {
        this.successMessage.set('Obavijest poslana.');
        this.selectedKorisnikId = null;
        this.naslov = '';
        this.poruka = '';
      },
      error: () => this.errorMessage.set('Greška pri slanju.'),
    });
  }
}
