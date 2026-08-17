import { Component, DestroyRef, inject, signal } from '@angular/core';
import {
  AbstractControl,
  NonNullableFormBuilder,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { ToastService } from '../../../../core/services/toast.service';
import { EventService } from '../../../../core/services/event.service';
import {
  EventDraftRequest,
  EventForm,
  EventStatusEnum,
  FoodProvidedEnum,
} from '../../../../core/models/event.model';
import { EventTypeEnum } from '../../../../core/models/event-type.model';
import { EventCreationView } from '../views/event-creation/event-creation.view';
import { LocationEnum } from '../../../../core/models/location.model';
import { dateRangeValidator } from '../../../../core/validators/date-range.validatior';

@Component({
  selector: 'app-event-creation-container',
  standalone: true,
  imports: [EventCreationView],
  template: `
    <app-event-creation-view
      [formGroup]="eventFormGroup"
      [isLoading]="isLoading()"
      [selectedType]="selectedType()"
      [posterName]="posterName()"
      (posterSelected)="handlePosterSelected($event)"
      (submitEvent)="handleEventSubmit()"
    />
  `,
})
export class EventCreationContainer {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly destroyRef = inject(DestroyRef);
  private readonly eventService = inject(EventService);
  private readonly toastService = inject(ToastService);
  private readonly translate = inject(TranslateService);

  readonly isLoading = signal(false);
  readonly selectedType = signal<EventTypeEnum | null>(null);
  readonly posterName = signal<string | null>(null);

  private eventId: number | null = null;
  private posterBase64: string | null = null;

  protected readonly eventFormGroup = this.fb.group<EventForm>(
    {
      title: this.fb.control('', Validators.required),
      description: this.fb.control('', Validators.required),
      startDate: this.fb.control<Date | null>(null, Validators.required),
      startTime: this.fb.control<Date | null>(null, Validators.required),
      endDate: this.fb.control<Date | null>(null, Validators.required),
      endTime: this.fb.control<Date | null>(null, Validators.required),
      registrationStartDate: this.fb.control<Date | null>(null, Validators.required),
      registrationStartTime: this.fb.control<Date | null>(null, Validators.required),
      registrationEndDate: this.fb.control<Date | null>(null, Validators.required),
      registrationEndTime: this.fb.control<Date | null>(null, Validators.required),
      type: this.fb.control<EventTypeEnum | null>(null, Validators.required),
      location: this.fb.control<LocationEnum | null>(null),
      foodProvided: this.fb.control<FoodProvidedEnum | null>(null),
    },
    { validators: dateRangeValidator }
  );

  constructor() {
    this.eventFormGroup.controls.type.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((type) => this.configureFieldsForType(type));
  }

  handlePosterSelected(file: File): void {
    if (!this.isValidPoster(file)) {
      this.toastService.showError(this.translate.instant('REGISTER.EVENT.POSTER.INVALID'));
      return;
    }

    const reader = new FileReader();

    reader.onload = () => {
      this.posterBase64 = reader.result as string;
      this.posterName.set(file.name);
    };

    reader.readAsDataURL(file);
  }

  handleEventSubmit(): void {
    if (this.eventFormGroup.invalid) {
      return;
    }

    const value = this.eventFormGroup.getRawValue();

    if (value.type === null || value.location === null) {
      return;
    }

    const payload: EventDraftRequest = {
      name: value.title,
      description: value.description,
      startTime: this.formatDateTime(value.startDate, value.startTime),
      endTime: this.formatDateTime(value.endDate, value.endTime),
      registrationStart: this.formatDateTime(
        value.registrationStartDate,
        value.registrationStartTime
      ),
      registrationEnd: this.formatDateTime(value.registrationEndDate, value.registrationEndTime),
      type: value.type,
      location: value.location,
      foodProvided: value.foodProvided === FoodProvidedEnum.YES,
      image: this.posterBase64,
      status: EventStatusEnum.DRAFT,
    };

    this.isLoading.set(true);

    const request =
      this.eventId === null
        ? this.eventService.createDraft(payload)
        : this.eventService.updateDraft(this.eventId, payload);

    request.pipe(finalize(() => this.isLoading.set(false))).subscribe({
      next: (event) => {
        this.eventId = event.id;
        this.toastService.showSuccess(
          this.translate.instant('REGISTER.EVENT.MESSAGES.SUCCESS.SAVE')
        );
      },
      error: () => {
        this.toastService.showError(this.translate.instant('REGISTER.EVENT.MESSAGES.ERROR.SAVE'));
      },
    });
  }

  private configureFieldsForType(type: EventTypeEnum | null): void {
    this.selectedType.set(type);

    const location = this.eventFormGroup.controls.location;
    const foodProvided = this.eventFormGroup.controls.foodProvided;

    location.clearValidators();
    foodProvided.clearValidators();

    if (type === EventTypeEnum.INTERNAL) {
      location.setValue(LocationEnum.ALL, { emitEvent: false });
      location.disable({ emitEvent: false });
      foodProvided.enable({ emitEvent: false });
      foodProvided.setValidators(Validators.required);
    } else if (type === EventTypeEnum.LOCAL) {
      location.enable({ emitEvent: false });

      if (location.value === LocationEnum.ALL) {
        location.setValue(null, { emitEvent: false });
      }

      location.setValidators(Validators.required);
      foodProvided.enable({ emitEvent: false });
      foodProvided.setValidators(Validators.required);
    } else if (type === EventTypeEnum.EXTERNAL) {
      location.enable({ emitEvent: false });

      if (location.value === LocationEnum.ALL) {
        location.setValue(null, { emitEvent: false });
      }

      location.setValidators(Validators.required);
      foodProvided.reset(null, { emitEvent: false });
      foodProvided.disable({ emitEvent: false });
    } else {
      location.reset(null, { emitEvent: false });
      foodProvided.reset(null, { emitEvent: false });
      location.enable({ emitEvent: false });
      foodProvided.disable({ emitEvent: false });
    }

    location.updateValueAndValidity({ emitEvent: false });
    foodProvided.updateValueAndValidity({ emitEvent: false });
  }

  private isValidPoster(file: File): boolean {
    const acceptedTypes = ['image/jpeg', 'image/png'];
    const maxSizeInBytes = 5 * 1024 * 1024;

    return acceptedTypes.includes(file.type) && file.size <= maxSizeInBytes;
  }

  private formatDateTime(date: Date | null, time: Date | null): string {
    if (date === null || time === null) {
      return '';
    }

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(time.getHours()).padStart(2, '0');
    const minutes = String(time.getMinutes()).padStart(2, '0');

    return `${year}-${month}-${day}T${hours}:${minutes}:00`;
  }
}
