import { Component, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { ForgotPasswordFormView } from '../views/forgot-password-form/forgot-password-form.view';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-forgot-password-form-container',
  standalone: true,
  imports: [ForgotPasswordFormView],
  template: `
    <app-forgot-password-form-view
      [formGroup]="forgotPasswordForm"
      [isLoading]="isLoading"
      (formSubmit)="onSubmit()"
    />
  `,
})
export class ForgotPasswordFormContainer {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly toastService = inject(ToastService);
  private readonly translateService = inject(TranslateService);
  private readonly router = inject(Router);

  isLoading = false;

  forgotPasswordForm = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
  });

  onSubmit(): void {
    if (this.forgotPasswordForm.invalid) {
      this.forgotPasswordForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;

    this.authService
      .forgotPassword(this.forgotPasswordForm.getRawValue())
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: () => {
          this.toastService.showSuccess(
            this.translateService.instant('FORGOT_PASSWORD.SUCCESS_MESSAGE')
          );
          this.router.navigate(['/login']);
        },
        error: (err: HttpErrorResponse) => {
          const errorKey =
            err.status === 404
              ? 'FORGOT_PASSWORD.EMAIL_NOT_FOUND'
              : 'FORGOT_PASSWORD.ERROR_MESSAGE';
          this.toastService.showError(this.translateService.instant(errorKey));
        },
      });
  }
}
