import { Component, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { LoginFormViewComponent } from '../views/login-form-view/login-form-view';
import { LoginRequest } from '../../../core/models/user-login.model';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-login-form-container',
  standalone: true,
  imports: [LoginFormViewComponent],
  template: `
    <app-login-form-view [formGroup]="loginForm" [isLoading]="isLoading" (formSubmit)="onLogin()" />
  `,
})
export class LoginFormContainerComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly toastService = inject(ToastService);
  private readonly translateService = inject(TranslateService);

  isLoading = false;

  loginForm = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
  });

  onLogin(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    const credentials: LoginRequest = this.loginForm.getRawValue();

    console.log(credentials);
  }
}
