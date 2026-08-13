import { Component, Input, Output, EventEmitter, inject } from '@angular/core';

@Component({
  selector: 'app-button-view',
  imports: [],
  template: `<button
    type="button"
    (click)="handleClick()"
    class="ml-3 px-3 py-2 font-medium text-brand-on-primary font-ui cursor-pointer hover:bg-white/20 hover:rounded transition-colors duration-200"
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
