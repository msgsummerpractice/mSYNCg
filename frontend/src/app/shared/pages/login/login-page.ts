import { Component } from '@angular/core';
import { LoginFormContainerComponent } from '../../components/containers/login-form-container/login-form-container';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [LoginFormContainerComponent],
  templateUrl: './login-page.html',
  styleUrl: './login-page.css',
})
export class LoginPageComponent {}
