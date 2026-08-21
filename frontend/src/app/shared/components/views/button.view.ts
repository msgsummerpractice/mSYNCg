import { Component, Input, Output, EventEmitter } from '@angular/core';
import { ButtonVariant } from '../containers/button.container';

@Component({
  selector: 'app-button-view',
  standalone: true,
  imports: [],
  template: `
    <button
      type="button"
      (click)="handleClick()"
      [class]="buttonClasses"
      class="ml-1 inline-flex max-w-full shrink-0 items-center justify-center whitespace-nowrap px-2 py-1 text-sm font-medium text-brand-on-primary font-ui cursor-pointer sm:ml-3 sm:px-3 sm:py-2 sm:text-sm"
      aria-label="action button"
    >
      {{ label }}
    </button>
  `,
})
export class ButtonView {
  @Input() label: string = '';
  @Input() variant: ButtonVariant = 'toolbar';

  @Output() clickEvent = new EventEmitter<void>();

  get buttonClasses(): string {
    const variantClasses: Record<ButtonVariant, string> = {
      toolbar:
        'ml-3 px-3 py-2 font-medium text-brand-on-primary font-ui cursor-pointer hover:bg-white/20 hover:rounded transition-colors duration-200',

      primary:
        'px-3 py-2 rounded-md font-medium bg-brand-primary text-brand-on-primary cursor-pointer hover:bg-[color-mix(in_srgb,var(--color-primary)_80%,white)] transition-colors duration-200',
    };

    return variantClasses[this.variant];
  }

  handleClick(): void {
    this.clickEvent.emit();
  }
}
