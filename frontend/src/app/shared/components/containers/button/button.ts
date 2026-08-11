import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-button-component',
  imports: [],
  templateUrl: '../../views/button/button.html',
  styleUrl: '../../views/button/button.css',
})
export class Button {
  @Input() label: string = '';
  @Input() route: string = '';

  constructor(private router: Router) {}

  handleClick(): void {
    this.router.navigate([this.route]);
  }

}
