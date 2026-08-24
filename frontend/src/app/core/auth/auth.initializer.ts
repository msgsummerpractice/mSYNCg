import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { firstValueFrom } from 'rxjs';
import { AuthService } from '../services/auth.service';

export function initializeAuth(): Promise<void> {
  const platformId = inject(PLATFORM_ID);
  const authService = inject(AuthService);

  if (!isPlatformBrowser(platformId)) {
    return Promise.resolve();
  }

  if (authService.hasToken() && !authService.hasValidSession()) {
    authService.logout();
    return Promise.resolve();
  }

  // Repopulate the in-memory current user signal on app start/reload, not just after login.
  return firstValueFrom(authService.loadCurrentUser()).then(() => undefined);
}
