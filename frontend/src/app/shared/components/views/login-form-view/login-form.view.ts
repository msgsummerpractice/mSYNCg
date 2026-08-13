import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { GenericFormView } from '../generic-form/generic-form.view';
import { RouterLink } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-login-form-view',
  imports: [
    ReactiveFormsModule,
    GenericFormView,
    TranslatePipe,
    RouterLink,
    MatFormFieldModule,
    MatInputModule,
  ],
  templateUrl: './login-form-view.html',
  styleUrl: './login-form.view.css',
})
export class LoginFormViewComponent {
  @Input({ required: true }) formGroup!: FormGroup;
  @Input() isLoading = false;

  @Output() formSubmit = new EventEmitter<void>();
}
