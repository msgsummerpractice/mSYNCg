import { CanActivateFn, Router } from '@angular/router';
import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { UserRole } from '../constants/role.constant';

export const adminGuard: CanActivateFn = () => {
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);

  if (!isPlatformBrowser(platformId)) {
    return true;
  }

  const accessToken =
    typeof localStorage === 'undefined' ? null : localStorage.getItem('accessToken');
  const userRole = getRoleFromToken(accessToken);

  if (userRole === UserRole.ADMIN) {
    return true;
  }

  return router.createUrlTree(['/events']);
};

function getRoleFromToken(accessToken: string | null): UserRole | null {
  if (!accessToken) {
    return null;
  }

  try {
    const payload = accessToken.split('.')[1];
    if (!payload) {
      return null;
    }

    const base64Payload = payload.replace(/-/g, '+').replace(/_/g, '/');
    const paddedPayload = base64Payload.padEnd(
      base64Payload.length + ((4 - (base64Payload.length % 4)) % 4),
      '='
    );
    const decodedPayload = JSON.parse(atob(paddedPayload));
    const role = decodedPayload.role?.replace(/^ROLE_/, '');

    return Object.values(UserRole).includes(role) ? role : null;
  } catch {
    return null;
  }
}
