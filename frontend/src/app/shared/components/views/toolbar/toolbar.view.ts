import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';

import { ButtonContainer } from '../../containers/button.container';
import { UserIconView } from '../../../../features/user/components/views/user-icon.view';

export interface NavItem {
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
  template: `
    <mat-toolbar>
      <button class="text-white" aria-label="msg logo">
        <img src="/msg_logo_color.svg" alt="msg logo" class="ml-3 h-18 w-18" />
      </button>

      <span class="example-spacer"></span>

      <ng-content></ng-content>

      @if (showNavigation) {
        <app-button-container
          *ngFor="let item of navItems"
          [label]="item.label"
          (clickEvent)="navigate.emit(item.route)"
        ></app-button-container>
      }

      @if (showUserIcon) {
        <app-user-icon-view></app-user-icon-view>
      }
    </mat-toolbar>
  `,
  styleUrl: '../../views/toolbar/toolbar.view.scss',
})
export class ToolbarView {
  @Input() showNavigation = true;
  @Input() showUserIcon = true;
  @Input() navItems: NavItem[] = [];

  @Output() navigate = new EventEmitter<string>();

  handleEventClick(route: string): void {
    this.navigate.emit(route);
  }
}
