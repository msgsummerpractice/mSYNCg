import { Component } from '@angular/core';
import { ForgotPasswordFormContainer } from '../../components/containers/forgot-password-form.container';
import { ToolbarView } from '../../components/views/toolbar/toolbar.view';
import { LanguageSwitcherContainer } from '../../components/containers/language-switcher.container';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-forgot-password-page',
  standalone: true,
  imports: [ForgotPasswordFormContainer, ToolbarView, LanguageSwitcherContainer, TranslatePipe],
  templateUrl: './forgot-password.page.html',
})
export class ForgotPasswordPage {}
