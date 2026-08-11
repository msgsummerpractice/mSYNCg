import { Routes } from '@angular/router';
import { UserList } from './features/admin/components/containers/user-list-component';
import { EventList } from './features/user/components/containers/event-list-component';

export const routes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        redirectTo: 'app'
    },
    {
        path: 'users',
        component: UserList
    },
    {
        path: 'events',
        component: EventList
    },
    {
        path: '**',
        redirectTo: 'app'
    }
];
