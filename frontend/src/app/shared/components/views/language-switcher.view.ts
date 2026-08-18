import { Component, EventEmitter, Input, Output } from '@angular/core';
import type { Language } from '../../../core/translation/supported-languages';
import { CommonModule } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';

@Component({
  selector: 'app-language-switcher-view',
  imports: [CommonModule, MatSelectModule, MatFormFieldModule],
  template: `
    <mat-form-field
      appearance="outline"
      class="language-switcher-field !h-8 !w-12 shrink-0 rounded-full bg-transparent [--mat-sys-on-surface:var(--color-on-primary)] [--mat-sys-on-surface-variant:var(--color-on-primary)] sm:!h-9 [&_.mat-mdc-text-field-wrapper]:!h-8 [&_.mat-mdc-text-field-wrapper]:!min-h-8 [&_.mat-mdc-text-field-wrapper]:!rounded-full [&_.mat-mdc-text-field-wrapper]:!bg-transparent [&_.mat-mdc-text-field-wrapper]:!px-2 [&_.mat-mdc-text-field-wrapper]:transition-colors [&_.mat-mdc-text-field-wrapper:hover]:!bg-white/20 sm:[&_.mat-mdc-text-field-wrapper]:!h-9 sm:[&_.mat-mdc-text-field-wrapper]:!min-h-9 [&_.mat-mdc-form-field-infix]:!min-h-0 [&_.mat-mdc-form-field-infix]:!w-auto [&_.mat-mdc-form-field-infix]:!p-0 [&_.mat-mdc-select-trigger]:!h-8 sm:[&_.mat-mdc-select-trigger]:!h-9"
      subscriptSizing="dynamic"
    >
      <mat-select
        [value]="currentLang"
        (selectionChange)="onSelectionChange($event.value)"
        panelClass="language-switcher-panel"
        panelWidth="auto"
      >
        <mat-option *ngFor="let lang of languages" [value]="lang.code">
          {{ lang.label }}
        </mat-option>
      </mat-select>
    </mat-form-field>
  `,
  styles: `
    .language-switcher-field .mdc-notched-outline,
    .language-switcher-field .mat-mdc-select-arrow-wrapper {
      display: none !important;
    }

    .language-switcher-field .mat-mdc-form-field-infix {
      min-width: 0 !important;
      padding: 0 !important;
      width: 100% !important;
    }

    .language-switcher-field .mat-mdc-select,
    .language-switcher-field .mat-mdc-select-trigger {
      width: 100%;
    }

    .language-switcher-field .mat-mdc-select-trigger {
      align-items: center;
      display: flex;
      justify-content: center;
    }

    .language-switcher-field .mat-mdc-select-value {
      flex: 0 1 auto;
      text-align: center;
    }

    .language-switcher-field .mat-mdc-select {
      font-size: var(--font-size-xs);
      font-weight: var(--font-weight-semibold);
      letter-spacing: 0;
      line-height: 1rem;
    }

    :host ::ng-deep .language-switcher-panel {
      background-color: #ffffff !important;
      min-width: 4.5rem !important;
    }

    :host ::ng-deep .language-switcher-panel .mat-mdc-option {
      --mat-option-label-text-color: #000000 !important;
    }

    :host ::ng-deep .language-switcher-panel .mat-mdc-option:hover {
      background-color: #f0f0f0;
    }

    :host ::ng-deep .language-switcher-panel .mat-mdc-option.mat-selected {
      background-color: #e0e0e0;
      --mat-option-label-text-color: #000000 !important;
    }
  `,
})
export class LanguageSwitcherView {
  @Input() languages: Language[] = [];
  @Input() currentLang: string = '';

  @Output() languageChange: EventEmitter<string> = new EventEmitter<string>();

  onSelectionChange(selectedLang: string): void {
    this.languageChange.emit(selectedLang);
  }
}
