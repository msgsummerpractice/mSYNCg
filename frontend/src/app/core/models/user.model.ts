import { UserRole } from '../constants/role.constant';
import { UserLocation } from '../constants/location.constant';

export interface User {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  location: UserLocation;
  role: UserRole;
  status: boolean;
}

export interface CurrentUser {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: UserRole;
}
