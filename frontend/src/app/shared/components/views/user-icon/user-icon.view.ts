import { Component, Input } from '@angular/core';
import { NgStyle } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-user-icon-view',
  imports: [NgStyle, MatIconModule, CommonModule],
  template: `
    <div class="flex min-w-0 items-center justify-center gap-1 sm:gap-2">
      <span
        class="max-w-24 truncate text-xs font-base text-brand-on-primary sm:mr-0.5 sm:max-w-40 sm:text-sm"
      >
        {{ userName }}
      </span>
      <div
        class="flex h-7 w-7 shrink-0 items-center justify-center rounded-full sm:h-8 sm:w-8"
        [ngStyle]="{ 'background-color': circleColor }"
      >
        <img *ngIf="userImage" [src]="userImage" class="h-5 w-5 rounded-full sm:h-6 sm:w-6" />
        <div
          *ngIf="!userImage"
          class="flex items-center text-center text-xs font-bold sm:text-sm"
          [ngStyle]="{ color: textColor }"
        >
          <span>{{ initials }}</span>
        </div>
      </div>
    </div>
  `,
})
export class UserIconView {
  @Input() userName: string = '';
  @Input() userImage?: string = '';

  @Input() showInitials: boolean = false;
  @Input() initials: string = '';
  @Input() circleColor: string = '#000';
  @Input() textColor: string = '#fff';
}
