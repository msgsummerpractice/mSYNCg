import { CanActivateFn, Router } from '@angular/router';
import { inject, PLATFORM_ID } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { isPlatformBrowser } from '@angular/common';
import { UserRole } from '../constants/role.constant';

export const createUpdateEventGuard: CanActivateFn = () => {
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);
  const service = inject(AuthService);

  if (!isPlatformBrowser(platformId)) {
    return true;
  }
  const userRole = service.getRole();

  if (
    userRole === UserRole.ADMIN ||
    userRole === UserRole.HR_USER ||
    userRole === UserRole.MARKETING_ORGANIZER
  ) {
    return true;
  }

  return router.createUrlTree(['/events/home']);
};
