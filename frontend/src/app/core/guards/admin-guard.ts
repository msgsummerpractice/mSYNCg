import { CanActivateFn, RedirectCommand, Router } from '@angular/router';
import { inject } from '@angular/core';

export const adminGuard: CanActivateFn = () => {
  const router = inject(Router);

  const userRole: string = 'PARTICIPANT'; // Replace with actual logic to get the user's role

  if (userRole === 'ADMIN') {
    return true;
  }

  switch (userRole) {
    case 'MARKETING_ORGANIZER':
      return router.createUrlTree(['events']);
    case 'PARTICIPANT':
      return router.createUrlTree(['events']);
    case 'HR_USER':
      return router.createUrlTree(['events']);
    default:
      return router.createUrlTree(['login']);
  }
};
