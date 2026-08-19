import { Routes } from '@angular/router';

import { LoginPage } from './shared/pages/login/login.page';
import { HomePage } from './shared/pages/home/home.page';
import { MainLayoutPage } from './shared/pages/main-layout/main-layout.page';
import UserRegisterPage from './shared/pages/user-register.page';
import { EventListPage } from './shared/pages/event-list/event-list.page';

import { UserListContainer } from './features/admin/components/containers/user-list.container';

import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin-guard';
import { eventManagementGuard } from './core/guards/event-management.guard';

export const routes: Routes = [
  // Default route
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

  // Protected application routes
  {
    path: '',
    component: MainLayoutPage,
    canActivate: [authGuard],
    children: [
      {
        path: 'home',
        component: HomePage,
      },

      // Event list
      {
        path: 'events',
        component: EventListPage,
      },

      // Event creation
      {
        path: 'events/create',
        canActivate: [eventManagementGuard],
        loadComponent: () =>
          import('./features/event/pages/event-creation.page').then(
            ({ EventCreationPage }) => EventCreationPage
          ),
      },

      // Event update
      {
        path: 'events/update/:id',
        canActivate: [eventManagementGuard],
        loadComponent: () =>
          import('./features/event/pages/event-update.page').then(
            ({ EventUpdatePage }) => EventUpdatePage
          ),
      },

      // Admin routes
      {
        path: 'admin',
        canActivate: [adminGuard],
        children: [
          {
            path: 'users',
            component: UserListContainer,
          },
        ],
      },
    ],
  },

  // Fallback
  {
    path: '**',
    redirectTo: '',
  },
];
