import { HttpClient } from '@angular/common/http';
import { TranslateLoader } from '@ngx-translate/core';

export class CustomTranslateLoader implements TranslateLoader {
  constructor(private http: HttpClient) {}

  getTranslation(lang: string) {
    return this.http.get<any>(`./messages/${lang}.json`);
  }
}
