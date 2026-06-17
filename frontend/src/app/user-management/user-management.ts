import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ManagementService, UserRecord } from '../services/management.service';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user-management.html',
  styleUrl: './user-management.css',
})
export class UserManagementComponent implements OnInit {
  users = signal<UserRecord[]>([]);
  showForm = signal(false);
  editingId = signal<number | null>(null);
  readonly roleOptions = ['admin', 'manager', 'tehnicar'];
  form: FormGroup;
  errorMessage = signal('');
  successMessage = signal('');

  constructor(
    private readonly fb: FormBuilder,
    private readonly mgmt: ManagementService
  ) {
    this.form = this.fb.group({
      username: ['', Validators.required],
      role: ['', Validators.required],
      password: [''],
      ime: [''],
      prezime: [''],
      email: ['']
    });
  }

  ngOnInit() { this.load(); }

  load() {
    this.mgmt.getUsers().subscribe({ next: (u) => this.users.set(u) });
  }

  openAdd() {
    this.editingId.set(null);
    this.form.reset();
    this.form.get('role')?.setValue('');
    this.form.get('password')?.setValidators(Validators.required);
    this.form.get('password')?.updateValueAndValidity();
    this.form.get('role')?.setValidators(Validators.required);
    this.form.get('role')?.updateValueAndValidity();
    this.showForm.set(true);
    this.errorMessage.set('');
  }

  openEdit(user: UserRecord) {
    this.editingId.set(user.id);
    this.form.patchValue(user);
    this.form.get('password')?.clearValidators();
    this.form.get('password')?.updateValueAndValidity();
    this.form.get('role')?.setValidators(Validators.required);
    this.form.get('role')?.updateValueAndValidity();
    this.showForm.set(true);
    this.errorMessage.set('');
  }

  cancel() { this.showForm.set(false); }

  onSubmit() {
    if (this.form.invalid) { this.errorMessage.set('Ispunite obavezna polja'); return; }
    this.errorMessage.set('');
    this.successMessage.set('');

    const data = { ...this.form.value };
    if (!data.password) delete data.password;

    const id = this.editingId();
    const obs = id ? this.mgmt.updateUser(id, data) : this.mgmt.createUser(data);
    obs.subscribe({
      next: () => {
        this.successMessage.set(id ? 'Korisnik ažuriran' : 'Korisnik kreiran');
        this.showForm.set(false);
        this.load();
      },
      error: (err) => this.errorMessage.set(err.error?.message || 'Greška')
    });
  }

  deactivate(id: number) {
    this.mgmt.deactivateUser(id).subscribe({
      next: () => { this.successMessage.set('Korisnik deaktiviran'); this.load(); }
    });
  }
}
