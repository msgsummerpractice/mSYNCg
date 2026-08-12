import { Component, EventEmitter, Input, Output } from '@angular/core';
import type { Language } from '../../../core/translation/supported-languages';
import { CommonModule } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';

@Component({
  selector: 'app-language-switcher-view',
  imports: [CommonModule, MatSelectModule, MatFormFieldModule],
  template: `<mat-form-field appearance="outline">
    <mat-label>Language</mat-label>
    <mat-select [value]="currentLang" (selectionChange)="onSelectionChange($event.value)">
      <mat-option *ngFor="let lang of languages" [value]="lang.code">
        {{ lang.label }}
      </mat-option>
    </mat-select>
  </mat-form-field> `,
})
export class LanguageSwitcherView {
  @Input() languages: Language[] = [];
  @Input() currentLang: string = '';

  @Output() languageChange: EventEmitter<string> = new EventEmitter<string>();

  onSelectionChange(selectedLang: string): void {
    this.languageChange.emit(selectedLang);
  }
}
