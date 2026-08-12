import { FormControl } from '@angular/forms';
import { LocationEnum } from './location.model';


export type UserRegisterForm = {
  firstName: FormControl<string>;
  lastName: FormControl<string>;
  email: FormControl<string>;
  password: FormControl<string>;
  confirmPassword: FormControl<string>;
  location: FormControl<LocationEnum | null>;
};

export interface UserRegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  location: LocationEnum;
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