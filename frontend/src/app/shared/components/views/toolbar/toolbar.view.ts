import { Component, Output, Input, signal } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ButtonContainer } from '../../containers/button.container';
import { UserIconContainer } from '../../containers/user-icon.container';
import { EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

interface NavItems {
  label: string;
  route: string;
}
@Component({
  selector: 'app-toolbar-view',
  imports: [
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    ButtonContainer,
    UserIconContainer,
    CommonModule,
  ],
  standalone: true,
  template: `<mat-toolbar
    class="flex h-auto min-h-14 flex-nowrap overflow-x-auto bg-brand-primary px-2 py-2 text-brand-on-primary font-ui sm:px-4"
  >
    <button
      class="shrink-0 px-1 py-1 font-medium text-brand-on-primary font-ui cursor-pointer sm:px-3 sm:py-2"
      aria-label="msg logo"
      (click)="navigate.emit('/home')"
    >
      <img
        src="{{ logoUrl }}"
        alt="msg logo"
        class="h-12 w-12 brightness-0 invert sm:h-14 sm:w-14"
      />
    </button>
    <span class="min-w-2 flex-1"></span>
    <div
      class="ml-auto flex shrink-0 flex-nowrap items-center gap-1 sm:gap-2 [&_app-button-container_button]:!ml-0"
    >
      <ng-container *ngIf="showNavigation">
        <app-button-container
          *ngFor="let item of navItems"
          [label]="item.label"
          (clickEvent)="navigate.emit(item.route)"
        ></app-button-container>
      </ng-container>
      <ng-content select="app-language-switcher"></ng-content>
      <app-user-icon-container
        *ngIf="showUserIcon"
        [userImage]="iconUrl()"
        [userName]="userName"
      ></app-user-icon-container>
      <app-button-container
        *ngIf="showLogoutButton"
        label="Logout"
        variant="primary"
        (clickEvent)="logout.emit()"
      ></app-button-container>
    </div>
  </mat-toolbar>`,
})
export class ToolbarView {
  @Output() navigate = new EventEmitter<string>();
  @Output() logout = new EventEmitter<void>();
  @Input() iconUrl = signal<string>('');
  @Input() navItems: NavItems[] = [
    { label: 'Events', route: '/events' },
    { label: 'Users', route: '/admin/users' },
  ];
  @Input()
  userName: string = '';
  @Input() showNavigation: boolean = true;
  @Input() showUserIcon: boolean = true;
  @Input() showLogoutButton: boolean = false;
  readonly logoUrl: string = '/assets/icons/msg_logo_color.svg';

  handleEventClick(route: string): void {
    this.navigate.emit(route);
  }
}
