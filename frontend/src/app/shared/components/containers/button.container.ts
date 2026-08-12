import { Component, Input, inject } from '@angular/core';
import { ButtonView } from '../views/button.view';
import { EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-button-container',
  imports: [ButtonView],
  template: `<app-button-view [label]="label" (clickEvent)="handleClick()"></app-button-view>`,
})
export class Button {
  @Input() label: string = '';
  @Output() clickEvent = new EventEmitter<void>();

  handleClick(): void {
    this.clickEvent.emit();
  }
}
