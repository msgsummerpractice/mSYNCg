import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { EVENT_TYPES, EventForm } from '../../../../core/models/event.model';
import { FormControl, ReactiveFormsModule, FormGroup } from '@angular/forms';
import { MatError } from '@angular/material/form-field';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { FoodTypeEnum } from '../../../../core/constants/food-type.constant';
import { MatSelectModule } from '@angular/material/select';
import { GenericFormView } from '../../../../shared/components/views/generic-form/generic-form.view';
import { MatInputModule } from '@angular/material/input';
import { Event } from '../../../../core/models/event.model';
import { MatButton } from '@angular/material/button';
import { ToolbarContainer } from '../../../../shared/components/containers/toolbar.container';
@Component({
  selector: 'app-user-event-register-view',
  imports: [
    TranslatePipe,
    MatSelectModule,
    GenericFormView,
    MatInputModule,
    MatCheckbox,
    MatSlideToggle,
    MatError,
    ReactiveFormsModule,
    MatButton,
    ToolbarContainer,
  ],
  templateUrl: './user-event-register.view.html',
})
export class UserEventRegisterView {
  @Input({ required: true }) formGroup!: FormGroup;
  @Input() isLoading = false;
  @Input() foodProvided: boolean | null = null;
  @Input() event: Event | null = null;

  @Output() submitEvent = new EventEmitter<void>();
  @Output() invalidSubmit = new EventEmitter<void>();
  @Output() cancelEvent = new EventEmitter<void>();

  readonly eventTypes = EVENT_TYPES;
  readonly foodPreferences = Object.values(FoodTypeEnum);
}
