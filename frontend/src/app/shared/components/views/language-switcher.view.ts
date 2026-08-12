import { Component, EventEmitter, Input, Output } from '@angular/core';
import type { Language } from '../../../core/translation/supported-languages';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-language-switcher-view',
  imports: [CommonModule, MatButtonModule, MatMenuModule, MatIconModule],
  template: `
    <button mat-button [matMenuTriggerFor]="langMenu">
      {{ currentLang.toUpperCase() }}
      <mat-icon>arrow_drop_down</mat-icon>
    </button>
    <mat-menu #langMenu="matMenu">
      <button mat-menu-item *ngFor="let lang of languages" (click)="onSelectionChange(lang.code)">
        {{ lang.code.toUpperCase() }}
      </button>
    </mat-menu>
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
