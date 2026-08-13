import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { ErrorStateMatcher } from '@angular/material/core';
import { TranslatePipe } from '@ngx-translate/core';
import { GenericFormContainer } from '../../containers/generic-form.container';
import { LocationEnum } from '../../../../core/models/location.model';
import { UserRegisterForm } from '../../../../core/models/user-register.model';

@Component({
  selector: 'user-register-view',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatInputModule,
    MatSelectModule,
    GenericFormContainer,
    TranslatePipe,
  ],
  templateUrl: './user-register.view.html',
})
export class UserRegisterView {
  @Input({ required: true }) formGroup!: FormGroup<UserRegisterForm>;
  @Input() isLoading = false;
  @Input() errorMessage = '';
  @Input() successMessage = '';
  @Input() mismatchMatcher!: ErrorStateMatcher;

  @Output() submitRegister = new EventEmitter<void>();

  readonly locations = Object.values(LocationEnum);
}
