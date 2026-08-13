import { Component, Output, Input } from '@angular/core';
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
  template: `<mat-toolbar class="bg-brand-primary text-brand-on-primary font-ui">
    <button
      class="ml-3 px-3 py-2 font-medium text-brand-on-primary font-ui cursor-pointer"
      aria-label="msg logo"
    >
      <img src="{{ logoUrl }}" alt="msg logo" class="ml-3 h-18 w-18 brightness-0 invert" />
    </button>
    <span class="flex-1"></span>
    <app-button-container
      *ngFor="let item of navItems"
      [label]="item.label"
      (clickEvent)="navigate.emit(item.route)"
    ></app-button-container>
    <app-user-icon-container [userImage]="iconUrl" [userName]="userName"></app-user-icon-container>
  </mat-toolbar>`,
})
export class ToolbarView {
  @Output() navigate = new EventEmitter<string>();
  @Input() userName: string = '';
  readonly iconUrl: string = '/assets/icons/user-icon.png';
  readonly logoUrl: string = '/assets/icons/msg_logo_color.svg';

  navItems: NavItems[] = [
    { label: 'Events', route: '/events' },
    { label: 'User', route: '/users' },
  ];

  handleEventClick(route: string): void {
    this.navigate.emit(route);
  }
}
