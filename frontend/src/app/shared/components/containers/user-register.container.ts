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
import { UserRegisterService } from '../../../core/services/user-register.service';
import {
  passwordMatchValidator,
  PasswordMismatchStateMatcher,
} from '../../../core/validators/password.validator';
import { LocationEnum } from '../../../core/models/location.model';
import { finalize } from 'rxjs/internal/operators/finalize';

@Component({
  selector: 'user-register-container',
  standalone: true,
  imports: [UserRegisterView],
  template: `<user-register-view
    [formGroup]="registerFormGroup"
    [isLoading]="isLoading()"
    [errorMessage]="errorMessage()"
    [successMessage]="successMessage()"
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
      location: this.fb.control<LocationEnum | null>(null, Validators.required),
    },
    { validators: passwordMatchValidator }
  );

  handleRegisterSubmit(): void {
    if (this.registerFormGroup.invalid) return;

    this.isLoading.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    const formValues = this.registerFormGroup.getRawValue();

    if (formValues.location === null) {
      this.isLoading.set(false);
      this.errorMessage.set(this.translate.instant('REGISTER.USER.MESSAGES.REQUIRED.LOCATION'));
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
          this.successMessage.set(
            this.translate.instant('REGISTER.USER.MESSAGES.SUCCESS.REGISTER')
          );
          setTimeout(() => this.router.navigate(['/login']), 2000);
        },
        //TODO: <update in order to adapt to HttpErrorResponse when connectiong FE to BE>
        error: (err) => {
          this.isLoading.set(false);
          const translatedError =
            typeof err?.error === 'string'
              ? this.translate.instant(err.error)
              : this.translate.instant('REGISTER.USER.MESSAGES.ERROR.REGISTER');
          this.errorMessage.set(translatedError);
        },
      });
  }
}
