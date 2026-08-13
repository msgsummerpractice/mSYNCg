export interface LoginRequest {
  email: string;
  password: string;
}

export enum UserRole {
  ADMIN = 'ADMIN',
  PARTICIPANT = 'PARTICIPANT',
  MARKETING_ORGANIZER = 'MARKETING_ORGANIZER',
  HR_USER = 'HR_USER',
}

export interface LoginResponse {
  token: string;
  userRole: UserRole;
}
