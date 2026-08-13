import { Component, Input } from '@angular/core';
import { NgStyle } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-user-icon-view',
  imports: [NgStyle, MatIconModule, CommonModule],
  template: `
    <div class="user-icon-container">
      <span>{{ userName }}</span>
      <div class="user-icon" [ngStyle]="{ 'background-color': circleColor }">
        <img *ngIf="userImage" [src]="userImage" />

        <div *ngIf="!userImage" class="initials ">
          <span>{{ initials }}</span>
        </div>
      </div>
    </div>
  `,
  styleUrl: '../user-icon/user-icon.view.scss',
})
export class UserIconView {
  @Input() userName: string = '';
  @Input() userImage?: string = '';

  @Input() showInitials: boolean = false;
  @Input() initials: string = '';
  @Input() circleColor: string = '#000';
}
