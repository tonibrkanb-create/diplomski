import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ManagementService, RecenzijaAdmin } from '../services/management.service';

@Component({
  selector: 'app-recenzije-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './recenzije-management.html',
  styleUrl: './recenzije-management.css',
})
export class RecenzijeManagementComponent implements OnInit {
  recenzije = signal<RecenzijaAdmin[]>([]);
  respondingId = signal<number | null>(null);
  odgovorText = '';
  successMessage = signal('');

  constructor(private readonly mgmt: ManagementService) {}

  ngOnInit() { this.load(); }

  load() {
    this.mgmt.getAllRecenzije().subscribe({ next: (r) => this.recenzije.set(r) });
  }

  startRespond(r: RecenzijaAdmin) {
    this.respondingId.set(r.id);
    this.odgovorText = r.odgovor || '';
  }

  cancelRespond() { this.respondingId.set(null); }

  submitResponse(id: number) {
    this.mgmt.respondToRecenzija(id, this.odgovorText).subscribe({
      next: () => {
        this.successMessage.set('Odgovor spremljen');
        this.respondingId.set(null);
        this.load();
      }
    });
  }

  getStars(ocjena: number): string {
    return '★'.repeat(ocjena) + '☆'.repeat(5 - ocjena);
  }
}
