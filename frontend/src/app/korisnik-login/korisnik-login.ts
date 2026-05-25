import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { KorisnikService } from '../services/korisnik.service';

@Component({
  selector: 'app-korisnik-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './korisnik-login.html',
  styleUrl: './korisnik-login.css',
})
export class KorisnikLoginComponent {
  email = '';
  password = '';
  errorMessage = signal('');

  constructor(
    private readonly korisnikService: KorisnikService,
    private readonly router: Router
  ) {}

  onLogin() {
    if (!this.email || !this.password) {
      this.errorMessage.set('Unesite email i lozinku');
      return;
    }

    this.errorMessage.set('');
    this.korisnikService.login(this.email, this.password).subscribe({
      next: () => {
        this.router.navigate(['/korisnik/dashboard']);
      },
      error: () => {
        this.errorMessage.set('Prijava nije uspjela. Provjerite podatke za prijavu.');
      }
    });
  }
}
