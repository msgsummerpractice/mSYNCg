import { Routes } from '@angular/router';
import { UserListContainer } from './features/admin/components/containers/user-list.container';
import { EventListContainer } from './features/event/components/containers/event-list.container';
import UserRegisterComponent from './shared/pages/user-register';

export const routes: Routes = [
  { path: '', redirectTo: 'events', pathMatch: 'full' },
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
    path: 'events',
    component: EventListContainer,
  },
  {
    path: 'events',
    redirectTo: 'events',
    pathMatch: 'full',
  },
  {
    path: 'register',
    component: UserRegisterComponent,
  },
];
