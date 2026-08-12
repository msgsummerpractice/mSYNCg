import { FormControl } from '@angular/forms';

export enum LocationEnum {
  CLUJ = 'CLUJ',
  TIMISOARA = 'TIMISOARA',
  MURES = 'MURES'
}

export type UserRegisterForm = {
  firstName: FormControl<string>;
  lastName: FormControl<string>;
  email: FormControl<string>;
  password: FormControl<string>;
  confirmPassword: FormControl<string>;
  location: FormControl<LocationEnum | null>;
};