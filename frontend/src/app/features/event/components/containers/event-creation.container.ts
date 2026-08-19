import { Component, DestroyRef, inject, signal } from '@angular/core';
import { AbstractControl, NonNullableFormBuilder, ValidatorFn, Validators } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { ToastService } from '../../../../core/services/toast.service';
import { EventService } from '../../../../core/services/event.service';
import { EventDraftRequest, EventForm, EventStatusEnum } from '../../../../core/models/event.model';
import { EventType } from '../../../../core/constants/event-type.constant';
import { EventCreationView } from '../views/event-creation/event-creation.view';
import { LocationEnum } from '../../../../core/models/location.model';
import { eventDateTimeRangeValidator } from '../../../../core/validators/time-range.validatior';
import { formatDateTime } from '../../../../core/utils/date.util';
import { EventStatus } from '../../../../core/constants/event-status.constant';

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
      (invalidSubmit)="handleInvalidForm()"
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
  readonly selectedType = signal<EventType | null>(null);
  readonly posterName = signal<string | null>(null);

  private eventId: number | null = null;
  private posterBase64: string | null = null;
  private readonly booleanRequiredValidator: ValidatorFn = (control) =>
    control.value === null || control.value === undefined ? { required: true } : null;

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
      type: this.fb.control<EventType | null>(null, Validators.required),
      location: this.fb.control<LocationEnum | null>(null),
      isFoodProvided: this.fb.control<boolean | null>(null),
    },
    { validators: eventDateTimeRangeValidator }
  );

  constructor() {
    this.eventFormGroup.controls.type.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((type) => this.configureFieldsForType(type));

    this.markEndControlTouchedWhenStartSelected(
      this.eventFormGroup.controls.startDate,
      this.eventFormGroup.controls.endDate
    );
    this.markEndControlTouchedWhenStartSelected(
      this.eventFormGroup.controls.startTime,
      this.eventFormGroup.controls.endTime
    );
    this.markEndControlTouchedWhenStartSelected(
      this.eventFormGroup.controls.registrationStartDate,
      this.eventFormGroup.controls.registrationEndDate
    );
    this.markEndControlTouchedWhenStartSelected(
      this.eventFormGroup.controls.registrationStartTime,
      this.eventFormGroup.controls.registrationEndTime
    );
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
      startTime: formatDateTime(value.startDate, value.startTime),
      endTime: formatDateTime(value.endDate, value.endTime),
      registrationStart: formatDateTime(
        value.registrationStartDate,
        value.registrationStartTime
      ),
      registrationEnd: formatDateTime(value.registrationEndDate, value.registrationEndTime),
      type: value.type,
      location: value.location,
      foodProvided: value.isFoodProvided ?? false,
      image: this.posterBase64,
      status: EventStatus.DRAFT,
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

  handleInvalidForm(): void {
    const errorKey = this.getFirstInvalidErrorKey();

    if (errorKey !== null) {
      this.toastService.showError(this.translate.instant(errorKey));
    }
  }

 private configureFieldsForType(type: EventType | null): void {
    this.selectedType.set(type);

    const location = this.eventFormGroup.controls.location;
    const isFoodProvided = this.eventFormGroup.controls.isFoodProvided;

    location.clearValidators();
    isFoodProvided.clearValidators();

    switch (type) {
      case EventType.INTERNAL:
        this.configureInternalType(location, isFoodProvided);
        break;
      case EventType.LOCAL:
        this.configureLocalType(location, isFoodProvided);
        break;
      case EventType.EXTERNAL:
        this.configureExternalType(location, isFoodProvided);
        break;
      default:
        this.configureDefaultType(location, isFoodProvided);
        break;
    }

    location.updateValueAndValidity({ emitEvent: false });
    isFoodProvided.updateValueAndValidity({ emitEvent: false });
  }

  private configureInternalType(location: AbstractControl, isFoodProvided: AbstractControl): void {
    location.setValue(LocationEnum.ALL, { emitEvent: false });
    location.disable({ emitEvent: false });
    isFoodProvided.enable({ emitEvent: false });
    isFoodProvided.setValidators(this.booleanRequiredValidator);
  }

  private configureLocalType(location: AbstractControl, isFoodProvided: AbstractControl): void {
    location.enable({ emitEvent: false });
    
    if (location.value === LocationEnum.ALL) {
      location.setValue(null, { emitEvent: false });
    }

    location.setValidators(Validators.required);
    isFoodProvided.enable({ emitEvent: false });
    isFoodProvided.setValidators(this.booleanRequiredValidator);
  }

  private configureExternalType(location: AbstractControl, isFoodProvided: AbstractControl): void {
    location.enable({ emitEvent: false });
    
    if (location.value === LocationEnum.ALL) {
      location.setValue(null, { emitEvent: false });
    }

    location.setValidators(Validators.required);
    isFoodProvided.reset(null, { emitEvent: false });
    isFoodProvided.disable({ emitEvent: false });
  }

  private configureDefaultType(location: AbstractControl, isFoodProvided: AbstractControl): void {
    location.reset(null, { emitEvent: false });
    isFoodProvided.reset(null, { emitEvent: false });
    location.enable({ emitEvent: false });
    isFoodProvided.disable({ emitEvent: false });
  }

  private markEndControlTouchedWhenStartSelected(
    startControl: AbstractControl,
    endControl: AbstractControl
  ): void {
    startControl.valueChanges.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((value) => {
      if (value !== null) {
        endControl.markAsTouched();
      }
    });
  }

  private getFirstInvalidErrorKey(): string | null {
    if (this.eventFormGroup.hasError('invalidDateRange')) {
      return 'REGISTER.EVENT.END_DATE.INVALID_RANGE';
    }

    if (this.eventFormGroup.hasError('invalidRegistrationDateRange')) {
      return 'REGISTER.EVENT.END_DATE.INVALID_RANGE';
    }

    const requiredErrorKeys: Record<string, string> = {
      title: 'REGISTER.EVENT.EVENT_TITLE.REQUIRED',
      description: 'REGISTER.EVENT.DESCRIPTION.REQUIRED',
      startDate: 'REGISTER.EVENT.START_DATE.REQUIRED',
      startTime: 'REGISTER.EVENT.START_TIME.REQUIRED',
      endDate: 'REGISTER.EVENT.END_DATE.REQUIRED',
      endTime: 'REGISTER.EVENT.END_TIME.REQUIRED',
      registrationStartDate: 'REGISTER.EVENT.REGISTRATION_START_DATE.REQUIRED',
      registrationStartTime: 'REGISTER.EVENT.REGISTRATION_START_TIME.REQUIRED',
      registrationEndDate: 'REGISTER.EVENT.REGISTRATION_END_DATE.REQUIRED',
      registrationEndTime: 'REGISTER.EVENT.REGISTRATION_END_TIME.REQUIRED',
      type: 'REGISTER.EVENT.TYPE.REQUIRED',
      location: 'REGISTER.EVENT.LOCATION.REQUIRED',
      isFoodProvided: 'REGISTER.EVENT.FOOD_PROVIDED.REQUIRED',
    };

    for (const controlName of Object.keys(requiredErrorKeys)) {
      const control = this.eventFormGroup.controls[controlName as keyof EventForm];

      if (control.hasError('required')) {
        return requiredErrorKeys[controlName];
      }
    }

    return null;
  }

  private isValidPoster(file: File): boolean {
    const acceptedTypes = ['image/jpeg', 'image/png'];
    const maxSizeInBytes = 5 * 1024 * 1024;

    return acceptedTypes.includes(file.type) && file.size <= maxSizeInBytes;
  }

}
