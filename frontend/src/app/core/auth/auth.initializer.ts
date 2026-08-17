import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { AuthService } from '../services/auth.service';

export function initializeAuth(): void {
  const platformId = inject(PLATFORM_ID);
  const authService = inject(AuthService);

  // Only run on browser (not during SSR)
  if (!isPlatformBrowser(platformId)) {
    return;
  }

  // Check if token exists and is expired
  if (authService.hasToken() && authService.isTokenExpired()) {
    authService.logout();
  }
}
