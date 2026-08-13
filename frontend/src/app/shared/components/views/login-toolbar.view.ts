import { Component } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { LanguageSwitcherContainer } from '../containers/language-switcher.container';

@Component({
  selector: 'app-login-toolbar-view',
  standalone: true,
  imports: [MatToolbarModule, LanguageSwitcherContainer],
  template: `
    <mat-toolbar>
      <span class="flex-1"></span>

      <app-language-switcher></app-language-switcher>
    </mat-toolbar>
  `,
})
export class LoginToolbarView {}
