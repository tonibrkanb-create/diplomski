import { Component, OnInit, signal } from '@angular/core';
import { ErrorBannerService } from './error-banner/error-banner.service';
import { ErrorBannerComponent } from './error-banner/error-banner';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';
import { AuthService } from './services/auth.service';
import { RadniNalogService, UskoroIsticeItem } from './services/radni-nalog.service';
import { SuccessModalService } from './services/success-modal.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, ErrorBannerComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  protected readonly title = signal('Atest Team');
  protected readonly year = new Date().getFullYear();
  protected readonly isEmbedded = signal(false);
  protected readonly isAuthenticated = signal(false);
  protected readonly isKorisnikRoute = signal(false);
  protected readonly uskoroIsticeCount = signal(0);
  protected readonly successModalOpen;
  protected readonly successModalMessage;
  protected readonly errorBannerOpen;
  protected readonly errorBannerMessage;

  constructor(
    private router: Router,
    private authService: AuthService,
    private radniNalogService: RadniNalogService,
    private successModalService: SuccessModalService,
    private errorBannerService: ErrorBannerService
  ) {
    this.successModalOpen = this.successModalService.isOpen;
    this.successModalMessage = this.successModalService.message;
    this.errorBannerOpen = this.errorBannerService.isOpen;
    this.errorBannerMessage = this.errorBannerService.message;

    this.updateEmbeddedMode();
    this.updateAuthState();
    this.refreshUskoroIsticeCount();
    this.updateKorisnikRoute();
    this.router.events
      .pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
      .subscribe(() => {
        this.updateEmbeddedMode();
        this.updateAuthState();
        this.updateKorisnikRoute();
        this.refreshUskoroIsticeCount();
      });
  }

  ngOnInit() {
    this.authService.unauthorizedEvent$.subscribe(() => {
      this.isAuthenticated.set(false);
      this.uskoroIsticeCount.set(0);

      if (this.isEmbedded() || this.isInsideIframe()) {
        return;
      }

      // Use setTimeout to let any in-flight resolver finish before navigating
      setTimeout(() => {
        this.router.navigate(['/login']);
      }, 0);
    });
  }

  protected logout() {
    this.authService.clearToken();

    this.isAuthenticated.set(false);
    this.uskoroIsticeCount.set(0);
    this.router.navigate(['/login']);
  }

  protected closeSuccessModal() {
    this.successModalService.close();
  }

  protected getSuccessModalIcon(): string {
    const msg = this.successModalService.message();
    // You can refine this logic as needed for your app's messages
    if (/uspješno|success|dodano|ažurirano|obrisano|registriran/i.test(msg)) {
      return '✅';
    }
    if (/nije uspjela|greška|error|failed|fail/i.test(msg)) {
      return '❌';
    }
    return 'ℹ️';
  }

  protected closeErrorBanner() {
    this.errorBannerService.close();
  }

  private updateEmbeddedMode() {
    const queryParams = this.router.parseUrl(this.router.url).queryParams;
    this.isEmbedded.set(queryParams['embedded'] === '1');
  }

  private isInsideIframe() {
    if (typeof window === 'undefined') {
      return false;
    }

    return window.self !== window.top;
  }

  private updateAuthState() {
    this.isAuthenticated.set(!!this.authService.getToken());
  }

  private updateKorisnikRoute() {
    this.isKorisnikRoute.set(this.router.url.startsWith('/korisnik'));
  }

  private refreshUskoroIsticeCount() {
    if (typeof window === 'undefined' || this.isEmbedded() || !this.isAuthenticated()) {
      this.uskoroIsticeCount.set(0);
      return;
    }

    this.radniNalogService.getUskoroIstice(15).subscribe({
      next: (result) => {
        const items = Array.isArray(result) ? result : (result ? [result] : []);
        const activeItems = items.filter((item: UskoroIsticeItem) => item?.isActive !== false);
        this.uskoroIsticeCount.set(activeItems.length);
      },
      error: () => {
        this.uskoroIsticeCount.set(0);
      }
    });
  }
}
