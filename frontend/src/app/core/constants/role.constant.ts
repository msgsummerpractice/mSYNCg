export enum UserRole {
  ADMIN = 'ADMIN',
  PARTICIPANT = 'PARTICIPANT',
  MARKETING_ORGANIZER = 'MARKETING_ORGANIZER',
  HR_USER = 'HR_USER',
}

export const USER_ROLES: UserRole[] = Object.values(UserRole);
