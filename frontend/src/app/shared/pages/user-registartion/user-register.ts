import { Component } from '@angular/core';
import { UserRegisterContainer } from '../../components/containers/user-register/user-register.container';

@Component({
  selector: 'user-register-page',
  standalone: true,
  imports: [UserRegisterContainer],
  template: '<user-register-container></user-register-container>',
})
export default class UserRegisterComponent {}