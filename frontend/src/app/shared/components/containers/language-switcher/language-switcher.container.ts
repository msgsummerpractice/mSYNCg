import { Component, inject } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { LanguageSwitcherView } from '../../views/language-swithcer/language-switcher.view';
import {
  SUPPORTED_LANGUAGES,
  SUPPORTED_LANGUAGE_CODES,
} from '../../../../core/translation/supported-languages';

@Component({
  selector: 'app-language-switcher',
  standalone: true,
  imports: [LanguageSwitcherView],
  template: `
    <app-language-switcher-view
      [languages]="languages"
      [currentLang]="currentLang"
      (languageChange)="onLanguageChange($event)"
    >
    </app-language-switcher-view>
  `,
})
export class LanguageSwitcherContainer {
  private translate = inject(TranslateService);

  languages = SUPPORTED_LANGUAGES;

  currentLang: string;

  constructor() {
    this.translate.addLangs(SUPPORTED_LANGUAGE_CODES);
    this.translate.setFallbackLang('en');

    const browserLang = this.translate.getBrowserLang();
    const isSupported = browserLang && SUPPORTED_LANGUAGE_CODES.includes(browserLang);
    this.currentLang = isSupported ? browserLang : 'en';

    this.translate.use(this.currentLang);
  }

  onLanguageChange(selectedLang: string): void {
    this.translate.use(selectedLang);
    this.currentLang = selectedLang;
  }
}
