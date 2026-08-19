import { Component, Input, OnInit } from '@angular/core';
import { NgStyle } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { UserIconView } from '../views/user-icon/user-icon.view';

@Component({
  selector: 'app-user-icon-container',
  imports: [NgStyle, MatIconModule, CommonModule, UserIconView],
  template: `<app-user-icon-view
    [userName]="userName"
    [userImage]="userImage"
    [initials]="initials"
    [circleColor]="circleColor"
    [textColor]="textColor"
  ></app-user-icon-view>`,
})
export class UserIconContainer implements OnInit {
  @Input() userName: string = '';
  @Input() userImage?: string = '';

  private colors: string[] = ['#800000', '#FF0000', '#FFA500', '#FFFF00', '#ebba45'];

  showInitials: boolean = true;
  initials: string = '';
  circleColor: string = '#000';
  textColor: string = '#fff';

  ngOnInit() {
    if (!this.userImage) {
      this.showInitials = true;
    }

    const randomIndex: number = Math.floor(Math.random() * this.colors.length);
    this.circleColor = this.colors[randomIndex];
    this.textColor = this.getContrastingTextColor(this.circleColor);
    this.initials = this.getInitials();
  }

  private getContrastingTextColor(hexColor: string): string {
    const hex = hexColor.replace('#', '');
    const r = parseInt(hex.substring(0, 2), 16);
    const g = parseInt(hex.substring(2, 4), 16);
    const b = parseInt(hex.substring(4, 6), 16);

    // relative luminance (WCAG formula)
    const luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255;
    return luminance > 0.6 ? '#000000' : '#ffffff';
  }

  private getInitials(): string {
    if (!this.userName) {
      this.initials = '';
      return '';
    }

    return this.userName
      .trim()
      .split(' ')
      .map((name) => name.charAt(0).toUpperCase())
      .join('')
      .toUpperCase()
      .substring(0, 2);
  }
}
