import { UserRole } from '../constants/role.constant';
import { UserLocation } from '../constants/location.constant';

export interface User {
  firstName: string;
  lastName: string;
  email: string;
  location: UserLocation;
  role: UserRole;
  status: boolean;
}
