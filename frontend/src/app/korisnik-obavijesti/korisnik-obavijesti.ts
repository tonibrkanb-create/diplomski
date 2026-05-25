import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { KorisnikService, Obavijest } from '../services/korisnik.service';

@Component({
  selector: 'app-korisnik-obavijesti',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './korisnik-obavijesti.html',
  styleUrl: './korisnik-obavijesti.css',
})
export class KorisnikObavijestiComponent implements OnInit {
  obavijesti = signal<Obavijest[]>([]);

  constructor(private readonly korisnikService: KorisnikService) {}

  ngOnInit() {
    this.loadObavijesti();
  }

  loadObavijesti() {
    this.korisnikService.getObavijesti().subscribe({
      next: (list) => this.obavijesti.set(list)
    });
  }

  markAsRead(id: number) {
    this.korisnikService.markAsRead(id).subscribe({
      next: () => this.loadObavijesti()
    });
  }
}
