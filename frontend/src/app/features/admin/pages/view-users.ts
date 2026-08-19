import { Component } from '@angular/core';
import { UserListContainer } from '../components/containers/user-list.container';
@Component({
  selector: 'view-users-page',
  standalone: true,
  imports: [UserListContainer],
  template: '<app-user-list-container></app-user-list-container>',
})
export default class ViewUsersComponent {}
