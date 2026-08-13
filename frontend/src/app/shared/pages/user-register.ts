import { Component } from '@angular/core';
import { UserRegisterContainer } from '../components/containers/user-register.container';
import { LanguageSwitcherContainer } from '../components/containers/language-switcher.container';
import { ToolbarView } from '../components/views/toolbar/toolbar.view';

@Component({
  selector: 'user-register-page',
  standalone: true,
  imports: [UserRegisterContainer, ToolbarView, LanguageSwitcherContainer],
  template: `
    <app-toolbar-view [showNavigation]="false" [showUserIcon]="false">
      <app-language-switcher></app-language-switcher>
    </app-toolbar-view>

    <user-register-container></user-register-container>
  `,
})
export default class UserRegisterComponent {}
