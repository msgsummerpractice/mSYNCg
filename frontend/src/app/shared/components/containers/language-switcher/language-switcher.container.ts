import { Component, inject } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { LanguageSwitcherView } from '../../views/language-switcher/language-switcher.view';
import type { Language } from '../../../../core/translation/supported-languages';
import { SUPPORTED_LANGUAGES } from '../../../../core/translation/supported-languages';

@Component({
  selector: 'app-language-switcher',
  standalone: true,
  imports: [LanguageSwitcherView],
  template: `
    <app-language-switcher-view
      [languages]="languages"
      [currentLang]="currentLang() || 'en'"
      (languageChange)="onLanguageChange($event)"
    >
    </app-language-switcher-view>
  `,
})
export class LanguageSwitcherContainer {
  private translate = inject(TranslateService);

  languages: Language[] = SUPPORTED_LANGUAGES;

  currentLang = this.translate.currentLang;

  onLanguageChange(selectedLang: string) {
    this.translate.use(selectedLang);
  }
}
