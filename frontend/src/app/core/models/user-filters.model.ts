import { UserRole } from '../constants/role.constant';
import { UserLocation } from '../constants/location.constant';

export interface UserFilterParams {
  firstName: string;
  lastName: string;
  email: string;
  roles: UserRole[];
  locations: UserLocation[];
  statuses: boolean[];
  page: number;
  size: number;
}
