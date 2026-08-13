import { Component } from '@angular/core';
import { LoginFormContainerComponent } from '../../components/containers/login-form-container';
import { ToolbarView } from '../../components/views/toolbar/toolbar.view';
import { LanguageSwitcherContainer } from '../../components/containers/language-switcher.container';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [LoginFormContainerComponent, ToolbarView, LanguageSwitcherContainer],
  templateUrl: './login-page.html',
  styleUrl: './login-page.css',
})
export class LoginPageComponent {}
