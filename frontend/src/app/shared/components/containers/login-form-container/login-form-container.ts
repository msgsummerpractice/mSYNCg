import { Component, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { LoginFormViewComponent } from '../../views/login-form-view/login-form-view';
import { LoginRequest } from '../../../../core/auth/auth-models';
import { AuthService } from '../../../../core/auth/auth-service';

@Component({
  selector: 'app-login-form-container',
  standalone: true,
  imports: [LoginFormViewComponent],
  templateUrl: './login-form-container.html',
  styleUrl: './login-form-container.css',
})
export class LoginFormContainerComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);

  isLoading = false;

  loginForm = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
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
