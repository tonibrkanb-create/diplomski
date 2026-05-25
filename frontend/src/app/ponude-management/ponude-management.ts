import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ManagementService, PonudaAdmin } from '../services/management.service';

@Component({
  selector: 'app-ponude-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ponude-management.html',
  styleUrl: './ponude-management.css',
})
export class PonudeManagementComponent implements OnInit {
  ponude = signal<PonudaAdmin[]>([]);
  selectedPonuda = signal<PonudaAdmin | null>(null);
  odgovorText = '';
  successMessage = signal('');

  constructor(private readonly mgmt: ManagementService) {}

  ngOnInit() { this.load(); }

  load() {
    this.mgmt.getAllPonude().subscribe({ next: (p) => this.ponude.set(p) });
  }

  select(p: PonudaAdmin) {
    this.selectedPonuda.set(p);
    this.odgovorText = p.odgovor || '';
  }

  close() { this.selectedPonuda.set(null); }

  updateStatus(status: string) {
    const p = this.selectedPonuda();
    if (!p) return;
    this.mgmt.updatePonudaStatus(p.id, status, this.odgovorText).subscribe({
      next: () => {
        this.successMessage.set('Status ažuriran');
        this.selectedPonuda.set(null);
        this.load();
      }
    });
  }

  getStatusLabel(status: string): string {
    const labels: Record<string, string> = { nova: 'Nova', poslana: 'Poslana', odobrena: 'Odobrena', odbijena: 'Odbijena' };
    return labels[status] || status;
  }
}
