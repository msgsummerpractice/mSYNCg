import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-button-view',
  standalone: true,
  imports: [],
  template: `
    <button
      type="button"
      (click)="handleClick()"
      [class]="buttonClasses"
      aria-label="action button"
    >
      {{ label }}
    </button>
  `,
})
export class ButtonView {
  @Input() label: string = '';
  @Input() variant: 'toolbar' | 'primary' = 'toolbar';

  @Output() clickEvent = new EventEmitter<void>();

  get buttonClasses(): string {
    if (this.variant === 'primary') {
      return 'px-3 py-2 rounded-md font-medium bg-brand-primary text-brand-on-primary cursor-pointer hover:opacity-90 transition';
    }

    return 'ml-3 px-3 py-2 font-medium text-brand-on-primary font-ui cursor-pointer hover:bg-white/20 hover:rounded transition-colors duration-200';
  }

  handleClick(): void {
    this.clickEvent.emit();
  }
}
