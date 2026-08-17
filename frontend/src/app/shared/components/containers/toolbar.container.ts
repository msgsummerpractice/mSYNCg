import { Component, inject, PLATFORM_ID } from '@angular/core';
import { ToolbarView } from '../views/toolbar/toolbar.view';
import { Router } from '@angular/router';
import { LanguageSwitcherContainer } from './language-switcher.container';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-toolbar-container',
  imports: [ToolbarView, LanguageSwitcherContainer],
  template: `<app-toolbar-view
    [userName]="userName"
    [showLogoutButton]="isLoggedIn"
    (navigate)="navigate($event)"
    (logout)="logout()"
    ><app-language-switcher></app-language-switcher
  ></app-toolbar-view>`,
})
export class ToolbarContainer {
  private router = inject(Router);
  private readonly authService = inject(AuthService);
  private readonly platformId = inject(PLATFORM_ID);
  userName: string = 'Test User';

  navigate(route: string): void {
    this.router.navigate([route]);
  }

  get isLoggedIn(): boolean {
    const isLoginPage = this.router.url === '/login';

    return this.authService.hasToken() && !isLoginPage;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
