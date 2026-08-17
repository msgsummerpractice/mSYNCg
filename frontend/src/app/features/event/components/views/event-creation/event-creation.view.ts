import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ReactiveFormsModule, FormGroup } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTimepickerModule } from '@angular/material/timepicker';
import { provideNativeDateAdapter } from '@angular/material/core';
import { TranslatePipe } from '@ngx-translate/core';
import { EventForm, FoodProvidedEnum } from '../../../../../core/models/event.model';
import { EventTypeEnum } from '../../../../../core/models/event-type.model';
import { GenericFormContainer } from '../../../../../shared/components/containers/generic-form.container';
import { LocationEnum } from '../../../../../core/models/location.model';

@Component({
  selector: 'app-event-creation-view',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
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
  @Output() posterSelected = new EventEmitter<File>();

  readonly eventTypes = EventTypeEnum;
  readonly types = Object.values(EventTypeEnum);
  readonly locations = [LocationEnum.CLUJ_NAPOCA, LocationEnum.TIMISOARA, LocationEnum.TARGU_MURES];
  readonly foodOptions = Object.values(FoodProvidedEnum);

  onPosterSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (file) {
      this.posterSelected.emit(file);
    }

    input.value = '';
  }
}
