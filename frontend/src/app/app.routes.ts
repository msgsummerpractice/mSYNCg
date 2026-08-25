import { Routes } from '@angular/router';
import { ResetPasswordPage } from './shared/pages/reset-password/reset-password.page';
import { LoginPage } from './shared/pages/login/login.page';
import { HomePage } from './shared/pages/home/home.page';
import { MainLayoutPage } from './shared/pages/main-layout/main-layout.page';
import UserRegisterPage from './shared/pages/user-register.page';
import { ForgotPasswordPage } from './shared/pages/forgot-password/forgot-password.page';
import { EventCardContainer } from './features/event/components/containers/event-card.container';
import { EventListPage } from './features/event/pages/event-list/event-list.page';

import { UserListContainer } from './features/admin/components/containers/user-list.container';

import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin-guard';
import { eventManagementGuard } from './core/guards/event-management.guard';
import { UserEventRegisterContainer } from './features/user/components/containers/user-event-register.container';
import { UserRegisterUpdateContainer } from './features/user/components/containers/user-register-update.container';

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
  {
    path: 'forgot-password',
    component: ForgotPasswordPage,
  },
  {
    path: 'reset-password',
    component: ResetPasswordPage,
  },
  {
    path: 'eventcard/:id',
    component: EventCardContainer,
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
      // Event creation
      {
        path: 'events/create',
        canActivate: [eventManagementGuard],
        loadComponent: () =>
          import('./features/event/pages/event-creation.page').then(
            ({ EventCreationPage }) => EventCreationPage
          ),
      },
      {
        path: 'events/:id/register',
        canActivate: [authGuard],
        component: UserEventRegisterContainer,
      },
      {
        path: 'events/:id/register/update',
        canActivate: [authGuard],
        component: UserRegisterUpdateContainer,
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
    ],
  },

  // Fallback
  {
    path: '**',
    redirectTo: '/events',
  },
];
