import { Routes } from '@angular/router';
import { UserList } from './features/admin/components/containers/user-list.container';
import { EventList } from './features/event/components/containers/event-list.container';
//import { HomePage } from './shared/pages/home/home';

export const routes: Routes = [
    // {
    //   path: '',
    //   component: HomePage,
    // },
    {path: '', redirectTo: 'app', pathMatch: 'full'},
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
        redirectTo: 'app',
        pathMatch: 'full'
    }
];
