import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';

@Component({
  selector: 'app-language-switcher',
  imports: [CommonModule, MatSelectModule, MatFormFieldModule],
  templateUrl: './language-switcher.html',
  styleUrls: ['./language-switcher.css'],
})
export class LanguageSwitcher {
  private translate = inject(TranslateService);

  languages = [
    { code: 'en', label: 'English' },
    { code: 'ro', label: 'Română' },
    { code: 'de', label: 'Deutsch' },
  ];

  currentLang: string;

  constructor() {
    this.translate.addLangs(['en', 'ro', 'de']);
    this.translate.setFallbackLang('en');

    const browserLang = this.translate.getBrowserLang();
    this.currentLang = browserLang?.match(/en|ro|de/) ? browserLang : 'en';

    this.translate.use(this.currentLang);
  }

  switchLanguage(selectedLang: string) {
    this.translate.use(selectedLang);
    this.currentLang = selectedLang;
  }
}
