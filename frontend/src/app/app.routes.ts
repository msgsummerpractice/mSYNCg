import { Routes } from '@angular/router';
import { LoginPage } from './shared/pages/login/login.page';
import { UserListContainer } from './features/admin/components/containers/user-list.container';
import { EventListContainer } from './features/event/components/containers/event-list.container';
import { adminGuard } from './core/guards/admin-guard';
import { HomePage } from './shared/pages/home/home.page';
import { MainLayoutPage } from './shared/pages/main-layout/main-layout.page';
import UserRegisterPage from './shared/pages/user-register.page';
import { EventCardContainer } from './features/event/components/containers/event-card.container';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
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
  {
    path: 'events',
    component: EventListContainer,
  },
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
    path: 'eventcard',
    component: EventCardContainer,
  },

  // Application layout
  {
    path: '',
    component: MainLayoutPage,
    canActivate: [authGuard],
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
        ({ EventCreationPage }) => EventCreationPage
      ),
  },

  // Fallback
  {
    path: '**',
    redirectTo: '',
  },
];
