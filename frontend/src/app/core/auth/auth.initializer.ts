import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { AuthService } from '../services/auth.service';

export function initializeAuth(): void {
  const platformId = inject(PLATFORM_ID);
  const authService = inject(AuthService);

  if (!isPlatformBrowser(platformId)) {
    return;
  }

  if (authService.hasToken() && !authService.hasValidSession()) {
    authService.logout();
  }
}
