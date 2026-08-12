import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';

import { GenericFormView } from '../generic-form/generic-form.view';

@Component({
  selector: 'app-login-form-view',
  standalone: true,
  imports: [ReactiveFormsModule, GenericFormView],
  templateUrl: './login-form-view.html',
  styleUrl: './login-form-view.css',
})
export class LoginFormViewComponent {
  @Input({ required: true }) formGroup!: FormGroup;
  @Input() isLoading = false;

  @Output() formSubmit = new EventEmitter<void>();
}
