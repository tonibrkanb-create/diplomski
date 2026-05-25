import { Component, Input, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-error-banner',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div *ngIf="error" class="error-banner" role="alert" tabindex="0">
      <span>{{ error }}</span>
    </div>
  `,
  styleUrls: ['./error-banner.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ErrorBannerComponent {
  @Input() error: string | null = null;
}
