import { FormControl } from '@angular/forms';

export enum LocationEnum {
  CLUJ_NAPOCA = 'Cluj-Napoca',
  TIMISOARA = 'Timișoara',
  TARGU_MURES = 'Târgu Mureș'
}

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