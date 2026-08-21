import { FormControl } from '@angular/forms';
import { LocationEnum } from './location.model';
import { UserLocation } from '../constants/location.constant';

export type UserRegisterForm = {
  firstName: FormControl<string>;
  lastName: FormControl<string>;
  email: FormControl<string>;
  password: FormControl<string>;
  confirmPassword: FormControl<string>;
  location: FormControl<UserLocation | null>;
};

export interface UserRegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  location: UserLocation;
  imageBase64?: string;
}

export interface UserRegisterResponse {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  location: string;
  status: boolean;
  imageUrlString: string;
  role: string;
}
