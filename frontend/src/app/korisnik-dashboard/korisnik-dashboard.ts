import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { KorisnikService, Korisnik, Obavijest } from '../services/korisnik.service';

@Component({
  selector: 'app-korisnik-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './korisnik-dashboard.html',
  styleUrl: './korisnik-dashboard.css',
})
export class KorisnikDashboardComponent implements OnInit {
  korisnik = signal<Korisnik | null>(null);
  neprocitaneObavijesti = signal(0);

  constructor(
    private readonly korisnikService: KorisnikService,
    private readonly router: Router
  ) {}

  ngOnInit() {
    this.korisnikService.getProfile().subscribe({
      next: (k) => this.korisnik.set(k),
      error: () => this.router.navigate(['/korisnik/prijava'])
    });

    this.korisnikService.getObavijesti().subscribe({
      next: (list) => this.neprocitaneObavijesti.set(list.filter(o => !o.procitana).length)
    });
  }

  logout() {
    this.korisnikService.logout();
    this.router.navigate(['/korisnik/prijava']);
  }
}
