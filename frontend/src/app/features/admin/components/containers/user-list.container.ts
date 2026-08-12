import { Component } from '@angular/core';
import { UserListView } from '../views/user-list.view';

@Component({
  selector: 'app-user-list',
  imports: [UserListView],
  template: '<app-user-list-view></app-user-list-view>',
})
export class UserList {}
