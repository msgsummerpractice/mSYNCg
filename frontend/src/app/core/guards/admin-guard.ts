import { CanActivateFn, Router } from '@angular/router';
import { inject, PLATFORM_ID } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { isPlatformBrowser } from '@angular/common';
import { UserRole } from '../constants/role.constant';

export const adminGuard: CanActivateFn = () => {
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);
  const service = inject(AuthService);

  if (!isPlatformBrowser(platformId)) {
    return true;
  }
  const userRole = service.getRole();

  if (userRole === UserRole.ADMIN) {
    return true;
  }

  return router.createUrlTree(['/events']);
};
