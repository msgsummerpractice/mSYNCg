import { inject, PLATFORM_ID } from '@angular/core';
import { Router } from '@angular/router';
import { map } from 'rxjs/internal/operators/map';
import { AuthService } from '../services/auth.service';
import { CanActivateFn } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';

export const LoggedInGuard: CanActivateFn = () => {
  const router = inject(Router);
  const authService = inject(AuthService);
  const platformId = inject(PLATFORM_ID);

  if (!isPlatformBrowser(platformId)) {
    return true;
  }

  if (!authService.hasValidSession()) {
    return true;
  }

  return authService.validateSession().pipe(
    map((isValid) => {
      if (isValid) {
        return router.createUrlTree(['/home']);
      }
      return true;
    })
  );
};
