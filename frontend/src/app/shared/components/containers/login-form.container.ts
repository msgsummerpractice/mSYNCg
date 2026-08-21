import { Component, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { finalize } from 'rxjs/operators';
import { LoginFormView } from '../views/login-form/login-form.view';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';
import { ToastService } from '../../../core/services/toast.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-login-form-container',
  standalone: true,
  imports: [LoginFormView],
  template: `
    <app-login-form-view [formGroup]="loginForm" [isLoading]="isLoading" (formSubmit)="onLogin()" />
  `,
})
export class LoginFormContainer {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly toastService = inject(ToastService);
  private readonly translateService = inject(TranslateService);
  private readonly router = inject(Router);

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
    this.isLoading = true;

    this.authService
      .login(this.loginForm.getRawValue())
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: () => {
          this.toastService.showSuccess(this.translateService.instant('LOGIN.LOGIN_SUCCESS'));
          this.router.navigate(['/home']);
        },
        error: (err: HttpErrorResponse) => {
          const errorKey =
            err.status === 403 ? 'LOGIN.ACCOUNT_INACTIVE_ERROR' : 'LOGIN.LOGIN_ERROR';
          this.toastService.showError(this.translateService.instant(errorKey));
        },
      });
  }
}
