export enum UserRole {
  ADMIN = 'ADMIN',
  PARTICIPANT = 'PARTICIPANT',
  MARKETING_ORGANIZER = 'MARKETING_ORGANIZER',
  HR_USER = 'HR_USER',
}

export const USER_ROLE_DISPLAY_VALUES: Record<UserRole, string> = {
  [UserRole.ADMIN]: 'Admin',
  [UserRole.PARTICIPANT]: 'Participant',
  [UserRole.MARKETING_ORGANIZER]: 'Marketing Organizer',
  [UserRole.HR_USER]: 'HR User',
};
