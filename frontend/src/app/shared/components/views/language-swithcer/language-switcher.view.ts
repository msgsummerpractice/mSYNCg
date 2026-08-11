import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';

@Component({
  selector: 'app-language-switcher-view',
  imports: [CommonModule, MatSelectModule, MatFormFieldModule],
  templateUrl: './language-switcher.view.html',
})
export class LanguageSwitcherView {
  @Input() languages: { code: string; label: string }[] = [];
  @Input() currentLang: string = '';

  @Output() languageChange = new EventEmitter<string>();

  onSelectionChange(selectedLang: string): void {
    this.languageChange.emit(selectedLang);
  }
}
