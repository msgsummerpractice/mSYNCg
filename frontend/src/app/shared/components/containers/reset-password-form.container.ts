import { Component, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

import { ResetPasswordFormView } from '../views/reset-password-form/reset-password-form.view';
import { ToastService } from '../../../core/services/toast.service';
import {
  passwordMatchValidator,
  PasswordMismatchStateMatcher,
} from '../../../core/validators/password.validator';
import { HttpErrorResponse } from '@angular/common/http';
import { finalize } from 'rxjs/internal/operators/finalize';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-reset-password-form-container',
  standalone: true,
  imports: [ResetPasswordFormView],
  template: `
    <app-reset-password-form-view
      [formGroup]="resetPasswordForm"
      [isLoading]="isLoading"
      [mismatchMatcher]="mismatchMatcher"
      (formSubmit)="onSubmit()"
    />
  `,
})
export class ResetPasswordFormContainer {
  private readonly formBuilder = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly toastService = inject(ToastService);
  private readonly translateService = inject(TranslateService);

  readonly mismatchMatcher = new PasswordMismatchStateMatcher();

  isLoading = false;

  readonly token = this.route.snapshot.queryParamMap.get('token');
  private readonly authService = inject(AuthService);

  resetPasswordForm = this.formBuilder.nonNullable.group(
    {
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]],
    },
    {
      validators: passwordMatchValidator,
    }
  );

  onSubmit(): void {
    if (this.resetPasswordForm.invalid) {
      this.resetPasswordForm.markAllAsTouched();
      return;
    }

    if (!this.token) {
      this.toastService.showError(this.translateService.instant('RESET_PASSWORD.INVALID_LINK'));
      return;
    }

    this.isLoading = true;

    const formValues = this.resetPasswordForm.getRawValue();

    this.authService
      .resetPassword({
        token: this.token,
        newPassword: formValues.password,
      })
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: () => {
          this.toastService.showSuccess(
            this.translateService.instant('RESET_PASSWORD.SUCCESS_MESSAGE')
          );

          this.router.navigate(['/login']);
        },
        error: (err: HttpErrorResponse) => {
          console.error('Reset password failed:', err);

          this.toastService.showError(
            this.translateService.instant('RESET_PASSWORD.ERROR_MESSAGE')
          );
        },
      });
  }
}
