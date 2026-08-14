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
      class="!h-8 !w-12 shrink-0 rounded-full bg-transparent [--mat-sys-on-surface:var(--color-on-primary)] [--mat-sys-on-surface-variant:var(--color-on-primary)] [--mdc-outlined-text-field-outline-color:transparent] [--mdc-outlined-text-field-hover-outline-color:transparent] [--mdc-outlined-text-field-focus-outline-color:transparent] sm:!h-9 sm:!w-16 [&_.mat-mdc-text-field-wrapper]:!h-8 [&_.mat-mdc-text-field-wrapper]:!min-h-8 [&_.mat-mdc-text-field-wrapper]:!rounded-full [&_.mat-mdc-text-field-wrapper]:!bg-transparent [&_.mat-mdc-text-field-wrapper:hover]:!bg-brand-on-primary-20 sm:[&_.mat-mdc-text-field-wrapper]:!h-9 sm:[&_.mat-mdc-text-field-wrapper]:!min-h-9 [&_.mat-mdc-form-field-infix]:!min-h-0 [&_.mat-mdc-form-field-infix]:!w-auto [&_.mat-mdc-form-field-infix]:!p-0 [&_.mat-mdc-select-trigger]:!h-8 sm:[&_.mat-mdc-select-trigger]:!h-9 [&_.mdc-notched-outline]:!border-0 [&_.mdc-notched-outline__leading]:!border-transparent [&_.mdc-notched-outline__notch]:!border-transparent [&_.mdc-notched-outline__trailing]:!border-transparent"
      subscriptSizing="dynamic"
    >
      <mat-select
        [value]="currentLang"
        (selectionChange)="onSelectionChange($event.value)"
        class="text-[0.6rem] sm:text-xs"
        panelClass="language-switcher-panel"
        panelWidth="auto"
      >
        <mat-option *ngFor="let lang of languages" [value]="lang.code">
          {{ lang.label }}
        </mat-option>
      </mat-select>
    </mat-form-field>
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
