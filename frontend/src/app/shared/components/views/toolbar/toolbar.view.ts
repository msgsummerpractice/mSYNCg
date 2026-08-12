import { Component, Output } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ButtonContainer } from '../../containers/button.container';
import { UserIconView } from '../../../../features/user/components/views/user-icon.view';
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
    UserIconView,
    CommonModule,
  ],
  template: `<mat-toolbar>
    <button class="text-brand-on-primary" aria-label="msg logo">
      <img src="msg_logo_color.svg" alt="msg logo" class="ml-3 h-18 w-18" />
    </button>
    <span class="example-spacer"></span>
    <app-button-container
      *ngFor="let item of navItems"
      [label]="item.label"
      (clickEvent)="navigate.emit(item.route)"
    ></app-button-container>
    <app-user-icon-view></app-user-icon-view>
  </mat-toolbar>`,
  styleUrl: '../../views/toolbar/toolbar.view.scss',
})
export class ToolbarView {
  @Output() navigate = new EventEmitter<string>();

  navItems: NavItems[] = [
    { label: 'Events', route: '/events' },
    { label: 'User', route: '/users' },
  ];

  handleEventClick(route: string): void {
    this.navigate.emit(route);
  }
}
