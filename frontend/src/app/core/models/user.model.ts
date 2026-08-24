import { UserRole } from '../constants/role.constant';
import { UserLocation } from '../constants/location.constant';
import { FormControl } from '@angular/forms';

export interface User {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  location: UserLocation;
  role: UserRole;
  status: boolean;
}

export interface UserProfileForm {
  firstName: FormControl<string | null>;
  lastName: FormControl<string | null>;
  email: FormControl<string | null>;
  location: FormControl<UserLocation | null>;
  role: FormControl<UserRole | null>;
  posterName: FormControl<string | null>;
}

export interface CurrentUser {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: UserRole;
  location: UserLocation;
}
