import { Routes } from '@angular/router';
import { HomePage } from './shared/pages/home/home';
import { LoginPageComponent } from './shared/pages/login/login-page';

export const routes: Routes = [
  {
    path: '',
    component: HomePage,
  },
  {
    path: 'login',
    component: LoginPageComponent,
  },
];
