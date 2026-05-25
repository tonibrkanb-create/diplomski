import { Component, Output, EventEmitter, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { SuccessModalService } from '../services/success-modal.service';

@Component({
  selector: 'app-register-user-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <form [formGroup]="form" (ngSubmit)="onSubmit()" class="register-user-form">
      <h3>Registriraj novog korisnika</h3>
      <div class="form-group">
        <label for="username">Korisničko ime</label>
        <input id="username" formControlName="username" required autocomplete="username" />
      </div>
      <div class="form-group">
        <label for="password">Lozinka</label>
        <input id="password" type="password" formControlName="password" required autocomplete="new-password" />
      </div>
      <div *ngIf="error" class="error">{{ error }}</div>
      <div *ngIf="success" class="success">Korisnik uspješno registriran!</div>
      <button type="submit" [disabled]="form.invalid || loading">Registriraj</button>
    </form>
  `,
  styleUrls: ['./register-user-form.css']
})
export class RegisterUserFormComponent {
  @Output() userRegistered = new EventEmitter<void>();
  form: FormGroup;
  loading = false;
  error: string | null = null;
  success = false;
  private successModal = inject(SuccessModalService);

  constructor(private fb: FormBuilder, private http: HttpClient) {
    this.form = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit() {
    if (this.form.invalid) return;
    this.loading = true;
    this.error = null;
    this.success = false;
    const { username, password } = this.form.value;
    this.http.post(`${environment.apiUrl}/auth/register`, { username, password }).subscribe({
      next: () => {
        this.success = true;
        this.loading = false;
        this.form.reset();
        this.userRegistered.emit();
        this.successModal.show('Korisnik uspješno registriran!');
      },
      error: (err) => {
        this.error = err?.error?.message || 'Greška prilikom registracije.';
        this.loading = false;
        this.successModal.show('Registracija nije uspjela!');
      }
    });
  }
}
