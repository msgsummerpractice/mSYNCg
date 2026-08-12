import { Component, inject } from '@angular/core';
import { ToolbarView } from '../views/toolbar/toolbar.view';
import { Router } from '@angular/router';
import { LanguageSwitcherContainer } from './language-switcher.container';

@Component({
  selector: 'app-toolbar-container',
  imports: [ToolbarView, LanguageSwitcherContainer],
  template: `<app-toolbar-view (navigate)="navigate($event)"
    ><app-language-switcher></app-language-switcher
  ></app-toolbar-view>`,
  styleUrl: '../views/toolbar/toolbar.view.scss',
})
export class ToolbarContainer {
  private router = inject(Router);

  navigate(route: string): void {
    this.router.navigate([route]);
  }
}
