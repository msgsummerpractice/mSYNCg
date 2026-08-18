import { Routes } from '@angular/router';
import { LoginPage } from './shared/pages/login/login.page';
import { HomePage } from './shared/pages/home/home.page';
import { MainLayoutPage } from './shared/pages/main-layout/main-layout.page';
import UserRegisterPage from './shared/pages/user-register.page';
import { EventListPage } from './shared/pages/event-list/event-list.page';
import { authGuard } from './core/guards/auth.guard';

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
    canActivate: [authGuard],
    children: [
      {
        path: 'events',
        children: [
          {
            path: '',
            component: EventListPage,
          },
          {
            path: 'home',
            component: HomePage,
          },
        ],
      },

      {
        path: 'admin',
        children: [],
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
