import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { CanActivateFn, Router } from '@angular/router';
import { map } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = () => {
  const router = inject(Router);
  const authService = inject(AuthService);
  const platformId = inject(PLATFORM_ID);

  if (!isPlatformBrowser(platformId)) {
    return true;
  }

  if (!authService.hasValidSession()) {
    authService.logout();
    return router.createUrlTree(['/login']);
  }

  return authService.validateSession().pipe(
    map((isValid) => {
      if (isValid) {
        return true;
      }

      authService.logout();
      return router.createUrlTree(['/login']);
    })
  );
};
