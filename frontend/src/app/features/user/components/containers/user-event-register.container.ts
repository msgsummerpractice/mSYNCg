import { Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { RegistrationService } from '../../../../core/services/registration.service';
import { UserEventRegisterView } from '../views/user-event-register.view';
import { EventRegistrationForm } from '../../../../core/models/event.model';
import { NonNullableFormBuilder, Validators } from '@angular/forms';
import { FoodTypeEnum } from '../../../../core/constants/food-type.constant';
import { EventTypeEnum } from '../../../../core/constants/event.constant';
import { LocationEnum } from '../../../../core/models/location.model';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { TranslateService } from '@ngx-translate/core';
import { ToastService } from '../../../../core/services/toast.service';
import { ActivatedRoute } from '@angular/router';
import { EventService } from '../../../../core/services/event.service';
import { Event } from '../../../../core/models/event.model';
import { Router } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';
import { formatDateTime } from '../../../../core/utils/date.util';
@Component({
  selector: 'app-user-event-register-container',
  standalone: true,
  imports: [UserEventRegisterView],
  template: `
    <app-user-event-register-view
      [formGroup]="eventFormGroup"
      [isLoading]="isLoading()"
      [event]="event()"
      [foodProvided]="foodProvided()"
      (submitEvent)="handleEventSubmit()"
      (invalidSubmit)="handleInvalidForm()"
      (cancelEvent)="handleCancel()"
    />
  `,
})
export class UserEventRegisterContainer implements OnInit {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly destroyRef = inject(DestroyRef);
  private readonly toastService = inject(ToastService);
  private readonly translate = inject(TranslateService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly eventService = inject(EventService);
  private readonly registrationService = inject(RegistrationService);
  private readonly authService = inject(AuthService);
  readonly isLoading = signal(false);
  readonly foodProvided = signal<boolean | null>(null);
  readonly event = signal<Event | null>(null);

  protected readonly eventFormGroup = this.fb.group<EventRegistrationForm>({
    type: this.fb.control<EventTypeEnum | null>(null, Validators.required),
    location: this.fb.control<LocationEnum | null>(null),
    transportNeeded: this.fb.control<boolean>(false),
    driverName: this.fb.control<string | null>(null),
    driverPhone: this.fb.control<string | null>(null),
    accommodationNeeded: this.fb.control<boolean>(false),
    accommodationDetails: this.fb.control<number | null>(null),
    photoConsent: this.fb.control<boolean>(false),
    GDPRConsent: this.fb.control<boolean>(false, Validators.requiredTrue),
    foodType: this.fb.control<FoodTypeEnum | null>(null),
  });

  ngOnInit() {
    this.setUpConditionValidation();

    const eventId = Number(this.route.snapshot.paramMap.get('id'));

    if (!Number.isInteger(eventId)) {
      return;
    }

    this.eventService.getEvent(eventId).subscribe({
      next: (event) => {
        this.event.set(event);

        this.eventFormGroup.patchValue({
          type: event.type,
          location: event.location,
        });

        if (event.foodProvided) {
          this.eventFormGroup.get('foodType')?.setValidators(Validators.required);
        } else {
          this.eventFormGroup.get('foodType')?.clearValidators();
          this.eventFormGroup.get('foodType')?.reset(null, { emitEvent: false });
        }
        this.eventFormGroup.get('foodType')?.updateValueAndValidity();
        this.foodProvided.set(event.foodProvided);

        const gdprControl = this.eventFormGroup.get('GDPRConsent');
        if (event.type === EventTypeEnum.INTERNAL || event.type === EventTypeEnum.LOCAL) {
          gdprControl?.setValidators(Validators.requiredTrue);
        } else {
          gdprControl?.clearValidators();
        }
        gdprControl?.updateValueAndValidity();
      },
      error: () => {
        const errorMsg = 'Nope';
        this.toastService.showError(errorMsg);
      },
    });
  }

  handleInvalidForm() {
    this.eventFormGroup.markAllAsTouched();

    const errorMsg = this.translate.instant('REGISTER_FOR_EVENT.INVALID_FORM');
    this.toastService.showError(errorMsg);
  }

  handleEventSubmit() {
    if (this.eventFormGroup.invalid) {
      return;
    }

    const eventId = Number(this.route.snapshot.paramMap.get('id'));
    if (!Number.isInteger(eventId)) {
      return;
    }

    const userId = this.authService.currentUser()?.id;
    if (userId === undefined) {
      this.toastService.showError(this.translate.instant('REGISTER_FOR_EVENT.ERROR'));
      return;
    }

    const form = this.eventFormGroup.getRawValue();

    this.isLoading.set(true);

    const req = {
      userId,
      eventId,
      date: formatDateTime(new Date()),
      gdpr: form.GDPRConsent,
      photoConsent: form.photoConsent,
      foodPreference: form.foodType,
      accommodationDays: form.accommodationDetails,
      driverName: form.driverName,
      driverPhone: form.driverPhone,
    };

    this.registrationService
      .registerForEvent(req)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.isLoading.set(false);
          this.toastService.showSuccess(this.translate.instant('REGISTER_FOR_EVENT.SUCCESS'));
          this.router.navigate(['/events']);
        },
        error: (error) => {
          this.isLoading.set(false);
          this.toastService.showError(this.translate.instant('REGISTER_FOR_EVENT.ERROR'));
        },
      });
  }

  handleCancel(): void {
    this.router.navigate([`/events`]);
  }

  private setUpConditionValidation(): void {
    const transportNeededControl = this.eventFormGroup.get('transportNeeded');
    const driverNameControl = this.eventFormGroup.get('driverName');
    const driverPhoneControl = this.eventFormGroup.get('driverPhone');

    transportNeededControl?.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((transportNeeded) => {
        if (transportNeeded) {
          driverNameControl?.setValidators([Validators.required]);
          driverPhoneControl?.setValidators([Validators.required]);
        } else {
          driverNameControl?.clearValidators();
          driverPhoneControl?.clearValidators();

          driverNameControl?.reset(null, { emitEvent: false });
          driverPhoneControl?.reset(null, { emitEvent: false });
        }

        driverNameControl?.updateValueAndValidity({ emitEvent: false });
        driverPhoneControl?.updateValueAndValidity({ emitEvent: false });
      });

    const accommodationNeededControl = this.eventFormGroup.get('accommodationNeeded');
    const accommodationDetailsControl = this.eventFormGroup.get('accommodationDetails');

    accommodationNeededControl?.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((accommodationNeeded) => {
        if (accommodationNeeded) {
          accommodationDetailsControl?.setValidators([Validators.required]);
        } else {
          accommodationDetailsControl?.clearValidators();
          accommodationDetailsControl?.reset(null, { emitEvent: false });
        }

        accommodationDetailsControl?.updateValueAndValidity({ emitEvent: false });
      });
  }
}
