import { Component } from '@angular/core';
import { ResetPasswordFormContainer } from '../../components/containers/reset-password-form.container';
import { ToolbarView } from '../../components/views/toolbar/toolbar.view';
import { LanguageSwitcherContainer } from '../../components/containers/language-switcher.container';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-reset-password-page',
  standalone: true,
  imports: [ResetPasswordFormContainer, ToolbarView, LanguageSwitcherContainer, TranslatePipe],
  templateUrl: './reset-password.page.html',
})
export class ResetPasswordPage {}
