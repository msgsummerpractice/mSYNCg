import { CanActivateFn, Router } from '@angular/router';
import { inject, PLATFORM_ID } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { isPlatformBrowser } from '@angular/common';
import { EVENT_MANAGEMENT_ROLES } from '../constants/role.constant';

export const eventManagementGuard: CanActivateFn = () => {
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);
  const service = inject(AuthService);

  if (!isPlatformBrowser(platformId)) {
    return true;
  }
  const userRole = service.getRole();

  if (userRole !== null && EVENT_MANAGEMENT_ROLES.includes(userRole)) {
    return true;
  }

  return router.createUrlTree(['/events/home']);
};
