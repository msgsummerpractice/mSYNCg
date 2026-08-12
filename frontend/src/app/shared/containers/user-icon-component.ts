import { Component, Input, OnInit } from '@angular/core';
import {NgStyle} from "@angular/common";
import {MatIconModule} from "@angular/material/icon";
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-user-icon-component',
  imports: [NgStyle, MatIconModule, CommonModule],
  templateUrl: '../views/user-icon-component.html',
  styleUrl: '../views/user-icon-component.css',
})
export class UserIcon implements OnInit {

  @Input() userName: string = '';
  @Input() userImage?: string = '';

  private colors = [
    '#800000',
    '#FF0000',
    '#FFA500',
    '#FFFF00',
    '#ebba45'
  ]

  public showInitials: boolean = false;
  public initials: string = '';
  public circleColor: string = '#000'; 

   
  ngOnInit() {
    if (!this.userImage) {
      this.showInitials = true;
      this.getInitials();
    }

    const randomIndex = Math.floor(Math.random() * this.colors.length);
    this.circleColor = this.colors[randomIndex];
  }
  
  getInitials(): string {
    if (!this.userName) {
      this.initials = '';
      return '';
    }

    return this.initials = this.userName
    .trim()
    .split(' ')
    .map(name => name.charAt(0).toUpperCase())
    .join('')
    .toUpperCase()
    .substring(0, 2);
  }



}
