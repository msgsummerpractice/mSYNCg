import { Routes } from '@angular/router';
import { HomePage } from './shared/pages/home/home';
import UserRegisterComponent from './shared/pages/user-registartion/user-register';

export const routes: Routes = [
  {
    path: '',
    component: HomePage,
  },
  {
    path: 'register',
    component: UserRegisterComponent,
  }
];
