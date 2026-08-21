import { Component, Input, inject } from '@angular/core';
import { ButtonView } from '../views/button.view';
import { EventEmitter, Output } from '@angular/core';

export type ButtonVariant = 'toolbar' | 'primary';
export type ButtonSize = 'md' | 'lg';

@Component({
  selector: 'app-button-container',
  imports: [ButtonView],
  template: `
    <app-button-view
      [label]="label"
      [variant]="variant"
      [size]="size"
      (clickEvent)="handleClick()"
    ></app-button-view>
  `,
})
export class ButtonContainer {
  @Input() label: string = '';
  @Input() variant: ButtonVariant = 'toolbar';
  @Input() size: ButtonSize = 'md';

  @Output() clickEvent = new EventEmitter<void>();

  handleClick(): void {
    this.clickEvent.emit();
  }
}
