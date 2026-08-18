import { Component, Input, inject } from '@angular/core';
import { ButtonView } from '../views/button.view';
import { EventEmitter, Output } from '@angular/core';

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
  @Input() variant: 'toolbar' | 'primary' = 'toolbar';

  @Output() clickEvent = new EventEmitter<void>();

  handleClick(): void {
    this.clickEvent.emit();
  }
}
