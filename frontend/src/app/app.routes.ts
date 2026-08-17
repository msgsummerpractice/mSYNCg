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

  // Public routes
  {
    path: 'login',
    component: LoginPage,
  },
  {
    path: 'register',
    component: UserRegisterPage,
  },

  // Application layout
  {
    path: '',
    component: MainLayoutPage,
    children: [
      {
        path: 'events',
        children: [
          {
            path: '',
            redirectTo: 'home',
            pathMatch: 'full',
          },
          {
            path: 'home',
            component: HomePage,
          },
        ],
      },
      {
        path: 'admin',
        children: [
          // admin routes here
        ],
      },
    ],
  },

  {
  path: 'events/create',
  loadComponent: () =>
    import('./features/event/pages/event-creation.page').then(
      (page) => page.default
    ),
  },

  // Fallback
  {
    path: '**',
    redirectTo: '',
  },
];
