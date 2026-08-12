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
import { UserRegisterForm } from '../../../../core/user-register/user-register.model';
import { UserRegisterView } from '../../views/user-register-view/user-register.view';
import { UserRegisterService } from '../../../../core/user-register/user-register-service';

export const passwordMatchValidator: ValidatorFn = (
  control: AbstractControl,
): ValidationErrors | null => {
  const password = control.get('password');
  const confirmPassword = control.get('confirmPassword');
  return password && confirmPassword && password.value === confirmPassword.value
    ? null
    : { passwordMismatch: true };
};

export class PasswordMismatchStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    return !!(control?.touched && form?.hasError('passwordMismatch'));
  }
};

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
  private readonly _fb = inject(NonNullableFormBuilder);
  private readonly _router = inject(Router);
  private readonly _registerService = inject(UserRegisterService);
  readonly mismatchMatcher = new PasswordMismatchStateMatcher();

  isLoading = signal<boolean>(false);
  errorMessage = signal<string>('');
  successMessage = signal<string>('');

  protected readonly registerFormGroup = this._fb.group<UserRegisterForm>(
    {
      firstName: this._fb.control('', [Validators.required]),
      lastName: this._fb.control('', [Validators.required]),
      email: this._fb.control('', [Validators.required, Validators.email]),
      password: this._fb.control('', [Validators.required, Validators.minLength(8)]),
      confirmPassword: this._fb.control('', [Validators.required]),
      location: this._fb.control(null, [Validators.required]),
    },
    { validators: passwordMatchValidator },
  );

  handleRegisterSubmit(): void {
    if (this.registerFormGroup.invalid) return;

    this.isLoading.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    const formValues = this.registerFormGroup.getRawValue();

    this._registerService.register(formValues).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.successMessage.set('Account created successfully! Redirecting...');

        setTimeout(() => this._router.navigate(['/login']), 2000);
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(err.error || 'An error occurred during registration.');
      },
    });
  }
}
