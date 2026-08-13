import { Component } from '@angular/core';
import { LoginFormContainer } from '../../components/containers/login-form.container';
import { ToolbarView } from '../../components/views/toolbar/toolbar.view';
import { LanguageSwitcherContainer } from '../../components/containers/language-switcher.container';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [LoginFormContainer, ToolbarView, LanguageSwitcherContainer, TranslatePipe],
  templateUrl: './login-page.html',
  styleUrl: './login-page.css',
})
export class LoginPageComponent {}
