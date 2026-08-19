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

export const USER_ROLE_TRANSLATION_KEYS: Record<UserRole, string> = {
  [UserRole.ADMIN]: 'USER_LIST.ROLES.ADMIN',
  [UserRole.HR_USER]: 'USER_LIST.ROLES.HR_USER',
  [UserRole.PARTICIPANT]: 'USER_LIST.ROLES.PARTICIPANT',
  [UserRole.MARKETING_ORGANIZER]: 'USER_LIST.ROLES.MARKETING_ORGANIZER',
};
export const USER_ROLES: UserRole[] = Object.values(UserRole);

export const EVENT_MANAGEMENT_ROLES: readonly UserRole[] = [
  UserRole.ADMIN,
  UserRole.HR_USER,
  UserRole.MARKETING_ORGANIZER,
];
