import { Routes } from '@angular/router';
import { LoginPageComponent } from './shared/pages/login/login-page';
import { UserListContainer } from './features/admin/components/containers/user-list.container';
import { EventListContainer } from './features/event/components/containers/event-list.container';
import { HomePageComponent } from './shared/pages/home/home-page';
import { MainLayoutComponent } from './shared/pages/main-layout/main-layout';
import UserRegisterComponent from './shared/pages/user-register';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full',
  },
  {
    path: 'login',
    component: LoginPageComponent,
  },
  {
    path: 'admin',
    children: [
      {
        path: 'users',
        component: UserListContainer,
      },
    ],
  },
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      {
        path: 'home',
        component: HomePageComponent,
      },
    ],
  },
  {
    path: 'events',
    component: EventListContainer,
  },
  {
    path: 'register',
    component: UserRegisterComponent,
  },
  {
    path: '**',
    redirectTo: '',
    pathMatch: 'full',
  },
];
