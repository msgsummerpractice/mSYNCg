import { Component, computed, inject, OnInit } from '@angular/core';
import { ToolbarView } from '../views/toolbar/toolbar.view';
import { Router } from '@angular/router';
import { LanguageSwitcherContainer } from './language-switcher.container';
import { AuthService } from '../../../core/services/auth.service';
import { UserRole } from '../../../core/constants/role.constant';

@Component({
  selector: 'app-toolbar-container',
  imports: [ToolbarView, LanguageSwitcherContainer],
  template: `<app-toolbar-view
    [userName]="userName()"
    [navItems]="navItems"
    [showUserIcon]="isLoggedIn"
    [showLogoutButton]="isLoggedIn"
    (navigate)="navigate($event)"
    (logout)="logout()"
    ><app-language-switcher></app-language-switcher
  ></app-toolbar-view>`,
})
export class ToolbarContainer implements OnInit {
  private router = inject(Router);
  private readonly authService = inject(AuthService);

  readonly userName = computed(() => {
    const user = this.authService.currentUser();
    return user ? `${user.firstName} ${user.lastName}` : '';
  });

  ngOnInit(): void {
    if (this.isLoggedIn && !this.authService.currentUser()) {
      this.authService.loadCurrentUser().subscribe();
    }
  }

  get navItems(): { label: string; route: string }[] {
    if (this.authService.hasRole(UserRole.ADMIN)) {
      return [
        { label: 'Events', route: '/events' },
        { label: 'Users', route: '/admin/users' },
      ];
    }

    return [{ label: 'Events', route: '/events' }];
  }

  navigate(route: string): void {
    this.router.navigate([route]);
  }

  get isLoggedIn(): boolean {
    return this.authService.hasValidSession();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
