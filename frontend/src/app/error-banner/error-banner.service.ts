import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ErrorBannerService {
  private readonly _isOpen = signal(false);
  private readonly _message = signal('');

  readonly isOpen = this._isOpen.asReadonly();
  readonly message = this._message.asReadonly();

  show(message: string): void {
    if (typeof window === 'undefined') {
      return;
    }
    this._message.set(message);
    this._isOpen.set(true);
  }

  close(): void {
    this._isOpen.set(false);
    this._message.set('');
  }
}
