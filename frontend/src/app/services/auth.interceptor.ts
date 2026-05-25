
import { HttpErrorResponse, HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, tap, throwError } from 'rxjs';
import { AuthService } from './auth.service';
import { SuccessModalService } from './success-modal.service';
import { ErrorBannerService } from '../error-banner/error-banner.service';


export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const authService = inject(AuthService);
  const successModalService = inject(SuccessModalService);
  const errorBannerService = inject(ErrorBannerService);
  const requestUrl = request.url.toLowerCase();
  const isLoginRequest = requestUrl.endsWith('/login') || requestUrl.includes('/auth/login');
  const isMutatingRequest = ['POST', 'PUT', 'DELETE'].includes(request.method.toUpperCase());
  const isBrowser = typeof window !== 'undefined';
  const isEmbeddedFrame = typeof window !== 'undefined' && window.self !== window.top;

  const requestToSend = (() => {
    if (isLoginRequest) {
      return request;
    }

    const token = authService.getToken();
    if (!token) {
      return request;
    }

    const headerToken = token.toLowerCase().startsWith('bearer ')
      ? token
      : `Bearer ${token}`;

    return request.clone({
      setHeaders: {
        Authorization: headerToken
      }
    });
  })();

  return next(requestToSend).pipe(
    tap((event) => {
      if (!isBrowser || !isMutatingRequest || isLoginRequest || !(event instanceof HttpResponse) || !event.ok) {
        return;
      }

      const successMessageByMethod: Record<string, string> = {
        POST: 'Uspješno dodano.',
        PUT: 'Uspješno ažurirano.',
        DELETE: 'Uspješno obrisano.'
      };

      const message = successMessageByMethod[request.method.toUpperCase()] || 'Uspješno spremljeno.';
      successModalService.show(message);
    }),
    catchError((error: unknown) => {
      const isUnauthorized = error instanceof HttpErrorResponse && error.status === 401;

      if (isBrowser && isUnauthorized && !isLoginRequest && !isEmbeddedFrame) {
        authService.notifyUnauthorized();
      }

      // Show error banner for 500 errors
      if (
        isBrowser &&
        error instanceof HttpErrorResponse &&
        error.status === 500 &&
        !isLoginRequest &&
        !isEmbeddedFrame
      ) {
        errorBannerService.show('Dogodila se greška na poslužitelju (500). Pokušajte ponovno kasnije.');
      }

      return throwError(() => error);
    })
  );
};