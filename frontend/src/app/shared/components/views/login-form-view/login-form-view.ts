import { Component, output } from '@angular/core';
import { LoginRequest } from '../../../../core/auth/auth-models';

@Component({
  selector: 'app-login-form-view',
  imports: [],
  templateUrl: './login-form-view.html',
  styleUrl: './login-form-view.css',
})
export class LoginFormViewComponent {
  login = output<LoginRequest>();
}
