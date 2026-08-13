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
  ></app-user-icon-view>`,
})
export class UserIconContainer implements OnInit {
  @Input() userName: string = '';
  @Input() userImage?: string = '';

  private colors: string[] = ['#800000', '#FF0000', '#FFA500', '#FFFF00', '#ebba45'];

  showInitials: boolean = false;
  initials: string = '';
  circleColor: string = '#000';

  ngOnInit() {
    if (!this.userImage) {
      this.showInitials = true;
    }

    const randomIndex: number = Math.floor(Math.random() * this.colors.length);
    this.circleColor = this.colors[randomIndex];
    this.initials = this.getInitials();
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
