import { Component, inject } from '@angular/core';
import { ToolbarView, NavItem } from '../views/toolbar/toolbar.view';
import { Router } from '@angular/router';
import { LanguageSwitcherContainer } from './language-switcher.container';

@Component({
  selector: 'app-toolbar-container',
  imports: [ToolbarView, LanguageSwitcherContainer],
  template: `<app-toolbar-view [userName]="userName" (navigate)="navigate($event)"
    ><app-language-switcher></app-language-switcher
  ></app-toolbar-view>`,
  styleUrl: '../views/toolbar/toolbar.view.scss',
})
export class ToolbarContainer {
  private router = inject(Router);
  userName: string = 'Test User';

  navItems: NavItem[] = [
    { label: 'Events', route: '/events' },
    { label: 'User', route: '/admin/users' },
  ];

  navigate(route: string): void {
    this.router.navigate([route]);
  }
}
