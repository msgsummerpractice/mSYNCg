import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ReactiveFormsModule, FormGroup } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTimepickerModule } from '@angular/material/timepicker';
import { ErrorStateMatcher, provideNativeDateAdapter } from '@angular/material/core';
import { TranslatePipe } from '@ngx-translate/core';
import { EventForm, EVENT_TYPES } from '../../../../../core/models/event.model';
import { EventTypeEnum } from '../../../../../core/constants/event.constant';
import { GenericFormContainer } from '../../../../../shared/components/containers/generic-form.container';
import { AVAILABLE_LOCATIONS } from '../../../../../core/models/location.model';

@Component({
  selector: 'app-event-creation-view',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatCheckboxModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatTimepickerModule,
    TranslatePipe,
    GenericFormContainer,
  ],
  providers: [provideNativeDateAdapter()],
  templateUrl: './event-creation.view.html',
})
export class EventCreationView {
  @Input({ required: true }) formGroup!: FormGroup<EventForm>;
  @Input() isLoading = false;
  @Input() selectedType: EventTypeEnum | null = null;
  @Input() posterName: string | null = null;

  @Output() submitEvent = new EventEmitter<void>();
  @Output() invalidSubmit = new EventEmitter<void>();
  @Output() posterSelected = new EventEmitter<File>();
  @Output() cancelEvent = new EventEmitter<void>();

  readonly eventTypeEnum = EventTypeEnum;
  readonly eventTypes = EVENT_TYPES;
  readonly locations = AVAILABLE_LOCATIONS;
  readonly eventRangeErrorStateMatcher: ErrorStateMatcher = {
    isErrorState: (control) => !!control?.parent?.hasError('invalidDateRange'),
  };
  readonly registrationRangeErrorStateMatcher: ErrorStateMatcher = {
    isErrorState: (control) => !!control?.parent?.hasError('invalidRegistrationDateRange'),
  };
  readonly startDatePastErrorStateMatcher: ErrorStateMatcher = {
    isErrorState: (control) => !!control?.parent?.hasError('startDateInPast'),
  };
  readonly registrationStartDatePastErrorStateMatcher: ErrorStateMatcher = {
    isErrorState: (control) => !!control?.parent?.hasError('registrationStartDateInPast'),
  };

  onPosterSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (file) {
      this.posterSelected.emit(file);
    }

    input.value = '';
  }
}
