import { inject } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { SUPPORTED_LANGUAGE_CODES } from './supported-languages';

export function initializeTranslation(): void {
  const translate = inject(TranslateService);

  translate.addLangs(SUPPORTED_LANGUAGE_CODES);
  translate.setFallbackLang('en');

  const browserLang = translate.getBrowserLang();

  const isSupported = !!browserLang && SUPPORTED_LANGUAGE_CODES.includes(browserLang);

  const currentLang = isSupported ? browserLang : 'en';

  translate.use(currentLang);
}
