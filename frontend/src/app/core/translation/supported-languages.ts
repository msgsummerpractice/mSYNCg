export interface Language {
  code: string;
  label: string;
}

export const SUPPORTED_LANGUAGES: Language[] = [
  { code: 'en', label: 'English' },
  { code: 'ro', label: 'Română' },
  { code: 'de', label: 'Deutsch' },
];

export const SUPPORTED_LANGUAGE_CODES = SUPPORTED_LANGUAGES.map((lang) => lang.code);
