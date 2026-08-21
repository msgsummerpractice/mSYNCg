import { Component, inject, signal } from '@angular/core';
import {
  NonNullableFormBuilder,
  Validators,
  AbstractControl,
  ValidationErrors,
  ValidatorFn,
  FormControl,
  FormGroupDirective,
  NgForm,
} from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { UserRegisterForm, UserRegisterRequest } from '../../../core/models/user-register.model';
import { UserRegisterView } from '../views/user-register/user-register.view';
import { AuthService as UserRegisterService } from '../../../core/services/auth.service';
import {
  passwordMatchValidator,
  PasswordMismatchStateMatcher,
} from '../../../core/validators/password.validator';
import { LocationEnum } from '../../../core/models/location.model';
import { ToastService } from '../../../core/services/toast.service';
import { finalize } from 'rxjs/internal/operators/finalize';
import { HttpErrorResponse } from '@angular/common/http';
import { UserLocation } from '../../../core/constants/location.constant';

@Component({
  selector: 'user-register-container',
  standalone: true,
  imports: [UserRegisterView],
  template: `<user-register-view
    [formGroup]="registerFormGroup"
    [isLoading]="isLoading()"
    [mismatchMatcher]="mismatchMatcher"
    (submitRegister)="handleRegisterSubmit()"
  >
  </user-register-view>`,
})
export class UserRegisterContainer {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly router = inject(Router);
  private readonly translate = inject(TranslateService);
  private readonly registerService = inject(UserRegisterService);
  private readonly toastService = inject(ToastService);
  readonly mismatchMatcher = new PasswordMismatchStateMatcher();

  isLoading = signal<boolean>(false);
  errorMessage = signal<string>('');
  successMessage = signal<string>('');

  protected readonly registerFormGroup = this.fb.group<UserRegisterForm>(
    {
      firstName: this.fb.control('', [Validators.required]),
      lastName: this.fb.control('', [Validators.required]),
      email: this.fb.control('', [Validators.required, Validators.email]),
      password: this.fb.control('', [Validators.required, Validators.minLength(8)]),
      confirmPassword: this.fb.control('', [Validators.required]),
      location: this.fb.control<UserLocation | null>(null, Validators.required),
    },
    { validators: passwordMatchValidator }
  );

  private extractBackendErrorMessage(err: unknown): string {
    const fallback = this.translate.instant('REGISTER.USER.MESSAGES.ERROR.REGISTER');

    if (!(err instanceof HttpErrorResponse)) {
      return fallback;
    }

    if (err.status === 0) {
      return this.translate.instant('REGISTER.USER.MESSAGES.ERROR.NETWORK');
    }

    const payload = err.error;

    if (typeof payload === 'string' && payload.trim().length > 0) {
      return payload;
    }

    if (payload && typeof payload === 'object') {
      const body = payload as BackendErrorResponse;

      if (Array.isArray(body.fieldErrors) && body.fieldErrors.length > 0) {
        return body.fieldErrors.map((f) => `${f.reason}`).join(' | ');
      }

      if (typeof body.message === 'string' && body.message.trim().length > 0) {
        return body.message;
      }
    }

    if (typeof err.message === 'string' && err.message.trim().length > 0) {
      return err.message;
    }

    return fallback;
  }

  handleRegisterSubmit(): void {
    if (this.registerFormGroup.invalid) return;

    this.isLoading.set(true);

    const formValues = this.registerFormGroup.getRawValue();

    if (formValues.location === null) {
      this.isLoading.set(false);
      const errorMsg = this.translate.instant('REGISTER.USER.MESSAGES.REQUIRED.LOCATION');
      this.toastService.showError(errorMsg);
      return;
    }

    const payload: UserRegisterRequest = {
      firstName: formValues.firstName,
      lastName: formValues.lastName,
      email: formValues.email,
      password: formValues.password,
      location: formValues.location,
    };

    this.registerService
      .register(payload)
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe({
        next: () => {
          this.isLoading.set(false);
          const successMsg = this.translate.instant('REGISTER.USER.MESSAGES.SUCCESS.REGISTER');
          this.toastService.showSuccess(successMsg, 5000);
          this.router.navigate(['/login']);
        },
        error: (err: unknown) => {
          const message = this.extractBackendErrorMessage(err);
          this.errorMessage.set(message);
          this.toastService.showError(message, 7000);
        },
      });
  }
}
