import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-button-component',
  imports: [],
  templateUrl: '../views/button-component.html',
  styleUrl: '../views/button-component.css',
})
export class Button {
  @Input() label: string = '';
  @Input() route: string = '';

  constructor(private router: Router) {}

  handleClick(): void {
    this.router.navigate([this.route]);
  }

}
