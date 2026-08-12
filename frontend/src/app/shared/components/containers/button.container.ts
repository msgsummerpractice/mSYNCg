import { Component, Input, inject } from '@angular/core';
import { Router } from '@angular/router';
import { ButtonView } from '../views/button.view';

@Component({
  selector: 'app-button-container',
  imports: [ButtonView],
  template: `<app-button-view [label]="label" (handleClickEvent)="handleClick()"></app-button-view>`,
})
export class Button {
  @Input() label: string = '';
  @Input() route: string = '';
  private router = inject(Router);

  handleClick(): void {
    this.router.navigate([this.route]);
  }

}
