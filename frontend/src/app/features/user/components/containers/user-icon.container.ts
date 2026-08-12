import { Component } from '@angular/core';
import { UserIconView } from '../views/user-icon.view';

@Component({
  selector: 'app-user-icon-container',
  imports: [UserIconView],
  template: `<app-user-icon-view></app-user-icon-view>`,
})
export class UserIconContainer {}
