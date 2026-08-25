import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { TranslatePipe } from '@ngx-translate/core';
import { ErrorStateMatcher } from '@angular/material/core';

import { GenericFormView } from '../generic-form/generic-form.view';

@Component({
  selector: 'app-reset-password-form-view',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatFormFieldModule,
    MatInputModule,
    TranslatePipe,
    GenericFormView,
  ],
  templateUrl: './reset-password-form.view.html',
})
export class ResetPasswordFormView {
  @Input({ required: true }) formGroup!: FormGroup;
  @Input() isLoading = false;
  @Input({ required: true }) mismatchMatcher!: ErrorStateMatcher;

  @Output() formSubmit = new EventEmitter<void>();
}
