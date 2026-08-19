import { Component } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { LanguageSwitcherContainer } from '../containers/language-switcher.container';

@Component({
  selector: 'app-login-toolbar-view',
  standalone: true,
  imports: [MatToolbarModule, LanguageSwitcherContainer],
  template: `
    <mat-toolbar class="flex h-auto min-h-14 justify-end px-2 py-2 sm:px-4">
      <span class="min-w-2 flex-1"></span>

      <app-language-switcher></app-language-switcher>
    </mat-toolbar>
  `,
})
export class LoginToolbarView {}
