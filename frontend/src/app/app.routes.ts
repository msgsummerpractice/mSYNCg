import { Routes } from '@angular/router';
import { LoginPage } from './shared/pages/login/login.page';
import { UserListContainer } from './features/admin/components/containers/user-list.container';
import { EventListContainer } from './features/event/components/containers/event-list.container';
import { HomePage } from './shared/pages/home/home.page';
import { MainLayoutPage } from './shared/pages/main-layout/main-layout.page';
import UserRegisterPage from './shared/pages/user-register.page';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full',
  },
  {
    path: 'login',
    component: LoginPage,
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
    component: MainLayoutPage,
    children: [
      {
        path: 'home',
        component: HomePage,
      },
    ],
  },
  {
    path: 'events',
    component: EventListContainer,
  },
  {
    path: 'register',
    component: UserRegisterPage,
  },
  {
    path: '**',
    redirectTo: '',
    pathMatch: 'full',
  },
];
