import { Routes } from '@angular/router';
import { UserList } from './features/admin/components/containers/user-list.container';
import { EventList } from './features/event/components/containers/event-list.container';

export const routes: Routes = [
  { path: '', redirectTo: 'events', pathMatch: 'full' },
  {
    path: 'admin',
    children: [
      {
        path: 'users',
        component: UserList,
      },
    ],
  },
  {
    path: 'events',
    component: EventList,
  },
  {
    path: '**',
    redirectTo: 'events',
    pathMatch: 'full',
  },
];
