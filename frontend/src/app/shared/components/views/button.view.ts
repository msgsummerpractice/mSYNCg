import { Component, Input, Output, EventEmitter, inject } from '@angular/core';

@Component({
  selector: 'app-button-view',
  imports: [],
  template: `<button
    type="button"
    (click)="handleClick()"
    class="ml-1 inline-flex max-w-full shrink-0 items-center justify-center whitespace-nowrap px-2 py-1 text-sm font-medium text-brand-on-primary font-ui cursor-pointer hover:bg-white/20 hover:rounded transition-colors duration-200 sm:ml-3 sm:px-3 sm:py-2 sm:text-sm"
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
