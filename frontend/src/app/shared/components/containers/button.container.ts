import { Component, Input, inject } from '@angular/core';
import { ButtonView } from '../views/button.view';
import { EventEmitter, Output } from '@angular/core';
import { ButtonVariant } from './button-variant.type';

@Component({
  selector: 'app-button-container',
  imports: [ButtonView],
  template: `
    <app-button-view
      [label]="label"
      [variant]="variant"
      (clickEvent)="handleClick()"
    ></app-button-view>
  `,
})
export class ButtonContainer {
  @Input() label: string = '';
  @Input() variant: ButtonVariant = 'toolbar';

  @Output() clickEvent = new EventEmitter<void>();

  handleClick(): void {
    this.clickEvent.emit();
  }
}
