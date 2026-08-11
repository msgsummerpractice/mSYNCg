import { Routes } from '@angular/router';
import { UserListComponent } from './features/admin/components/containers/user-list-component';
import { EventListComponent } from './features/user/components/containers/event-list-component';

export const routes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        redirectTo: 'app'
    },
    {
        path: 'users',
        component: UserListComponent
    },
    {
        path: 'events',
        component: EventListComponent
    },
    {
        path: '**',
        redirectTo: 'app'
    }
];
