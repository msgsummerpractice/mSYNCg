import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { TranslatePipe } from '@ngx-translate/core';
import { GenericFormView } from '../generic-form/generic-form.view';

@Component({
  selector: 'app-forgot-password-form-view',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatFormFieldModule,
    MatInputModule,
    TranslatePipe,
    GenericFormView,
  ],
  templateUrl: './forgot-password-form.view.html',
})
export class ForgotPasswordFormView {
  @Input({ required: true }) formGroup!: FormGroup;
  @Input() isLoading: boolean = false;

  @Output() formSubmit = new EventEmitter<void>();
}
