import { Component, Input, Output, EventEmitter, inject } from '@angular/core';

@Component({
  selector: 'app-button-view',
  imports: [],
  template: `<button
    type="button"
    (click)="handleClick()"
    class="ml-3 font-medium hover:text-brand-on-primary"
    aria-label="events button"
    >{{ label }}</button
  >`,
})
export class ButtonView {
  @Input() label: string = '';
  @Output() clickEvent: EventEmitter<void> = new EventEmitter<void>();

  handleClick(): void {
    this.clickEvent.emit();
  }
}
