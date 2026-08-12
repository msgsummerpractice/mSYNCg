import { Component, inject } from '@angular/core';
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
  private readonly authService = inject(AuthService);

  onLogin(credentials: LoginRequest): void {
    console.log('Login data:', credentials);
  }
}
