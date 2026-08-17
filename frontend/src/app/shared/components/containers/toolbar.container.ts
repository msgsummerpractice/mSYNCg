import { Component, inject } from '@angular/core';
import { ToolbarView } from '../views/toolbar/toolbar.view';
import { Router } from '@angular/router';
import { LanguageSwitcherContainer } from './language-switcher.container';

@Component({
  selector: 'app-toolbar-container',
  imports: [ToolbarView, LanguageSwitcherContainer],
  template: `<app-toolbar-view [userName]="userName" (navigate)="navigate($event)"
    ><app-language-switcher></app-language-switcher
  ></app-toolbar-view>`,
})
export class ToolbarContainer {
  private router = inject(Router);
  userName: string = 'Test User';

  navigate(route: string): void {
    this.router.navigate([route]);
  }
}
