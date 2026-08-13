import { Component, Input } from '@angular/core';
import { NgStyle } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-user-icon-view',
  imports: [NgStyle, MatIconModule, CommonModule],
  template: `
    <div class="flex items-center justify-center gap-0.75 ml-6">
      <span class="mr-0.5 text-sm font-base text-brand-on-primary">{{ userName }}</span>
      <div
        class="rounded-full w-8 h-8 flex justify-center items-center"
        [ngStyle]="{ 'background-color': circleColor }"
      >
        <img *ngIf="userImage" [src]="userImage" class="w-6 h-6 rounded-full" />
        <div *ngIf="!userImage" class="text-sm text-white font-bold flex items-center text-center">
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
