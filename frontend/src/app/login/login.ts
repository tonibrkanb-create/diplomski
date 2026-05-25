import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  username = '';
  password = '';
  errorMessage = signal('');

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router
  ) {}

  onLogin() {
    if (!this.username || !this.password) {
      this.errorMessage.set('Unesite korisničko ime i lozinku');
      return;
    }

    this.errorMessage.set('');
    console.log('Attempting login with', this.username, this.password);
    this.authService.login(this.username, this.password).subscribe({
      next: () => {
        this.router.navigate(['/radniNalozi']);
      },
      error: (error) => {
        console.error('Login failed:', error);
        this.errorMessage.set('Prijava nije uspjela. Provjerite podatke za prijavu.');
      }
    });
  }
}
