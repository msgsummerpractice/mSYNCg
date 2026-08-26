import { Component, Input, Output, EventEmitter } from '@angular/core';
import { ButtonVariant, ButtonSize } from '../containers/button.container';

@Component({
  selector: 'app-button-view',
  standalone: true,
  imports: [],
  template: ` <button
    type="button"
    (click)="handleClick()"
    [disabled]="disabled"
    [class]="buttonClasses"
    class="ml-1 inline-flex max-w-full shrink-0 items-center justify-center whitespace-nowrap font-medium text-brand-on-primary font-ui sm:ml-3"
    aria-label="action button"
  >
    @if (label) {
      <span>{{ label }}</span>
    }
  </button>`,
})
export class ButtonView {
  @Input() label: string = '';
  @Input() variant: ButtonVariant = 'toolbar';
  @Input() size: ButtonSize = 'md';
  @Input() disabled: boolean = false;

  @Output() clickEvent = new EventEmitter<void>();

  get buttonClasses(): string {
    if (this.disabled) {
      return `rounded-md font-medium bg-gray-300 text-gray-500 cursor-not-allowed ${this.sizeClasses}`;
    }

    const variantClasses: Record<ButtonVariant, string> = {
      toolbar:
        'font-medium text-brand-on-primary font-ui cursor-pointer hover:bg-white/20 hover:rounded transition-colors duration-200',

      primary:
        'rounded-md font-medium bg-brand-primary text-brand-on-primary cursor-pointer hover:bg-[color-mix(in_srgb,var(--color-primary)_80%,white)] transition-colors duration-200',
    };

    return `${variantClasses[this.variant]} ${this.sizeClasses}`;
  }

  private get sizeClasses(): string {
    const sizeClasses: Record<ButtonSize, string> = {
      md: 'px-2 py-1 text-sm sm:px-3 sm:py-2 sm:text-sm',
      lg: 'px-4 py-2.5 text-base sm:px-5 sm:py-3 sm:text-base',
    };

    return sizeClasses[this.size];
  }

  handleClick(): void {
    this.clickEvent.emit();
  }
}
