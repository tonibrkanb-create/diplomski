import { CommonModule } from '@angular/common';
import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-confirm-delete-popup',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './confirm-delete-popup.html',
  styleUrls: ['./confirm-delete-popup.css']
})
export class ConfirmDeletePopupComponent {
  readonly title = input('Obriši radni nalog');
  readonly message = input('Jeste li sigurni da želite obrisati ovu stavku?');
  readonly confirmLabel = input('Da');
  readonly cancelLabel = input('Ne');
  readonly loading = input(false);

  readonly confirm = output<void>();
  readonly cancel = output<void>();

  onConfirm() {
    if (this.loading()) {
      return;
    }

    this.confirm.emit();
  }

  onCancel() {
    if (this.loading()) {
      return;
    }

    this.cancel.emit();
  }
}
